/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directmemory.lightning.jgroups;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.exceptions.ClassDefinitionInconsistentException;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class LightningJGroupsMembershipListener
    extends ReceiverAdapter
{

    private final List<Address> lastMembersView = new ArrayList<Address>();

    private final ExecutorService executorService;

    private final Serializer serializer;

    private final JChannel channel;

    public LightningJGroupsMembershipListener( JChannel channel, Serializer serializer, ExecutorService executorService )
    {

        this.channel = channel;
        this.serializer = serializer;
        this.executorService = executorService;
    }

    @Override
    public void viewAccepted( View view )
    {
        Runnable task;
        if ( view instanceof MergeView )
        {
            task = handleSplitBrainMerge( view );
        }
        else
        {
            task = handleMemberJoin( view );
        }

        if ( task != null )
        {
            executorService.submit( task );
        }
    }

    @Override
    public void receive( Message msg )
    {
        // If we received a ClassDefinitionContainer handle it otherwise just
        // ignore the message
        if ( msg.getObject() instanceof ClassDefinitionContainer )
        {
            ClassDefinitionContainer container = (ClassDefinitionContainer) msg.getObject();
            try
            {
                serializer.setClassDefinitionContainer( container );
            }
            catch ( ClassDefinitionInconsistentException e )
            {
                channel.disconnect();
                throw new LightningClusterException( "Class checksums are not consistent, channel disconnected", e );
            }
        }
    }

    private Runnable handleMemberJoin( View view )
    {
        List<Address> members = view.getMembers();

        // Quote from JGroups documentation:
        // *Note that the first member of a view is the coordinator (the one who
        // emits new views).*
        Address coordinator = members.get( 0 );

        if ( channel.getAddress().equals( coordinator ) )
        {
            final ClassDefinitionContainer container = serializer.getClassDefinitionContainer();
            final List<Address> receivers = findNewMembers( view );

            try
            {
                final byte[] byteBuffer = Util.objectToByteBuffer( container );

                return new Runnable()
                {

                    @Override
                    public void run()
                    {
                        for ( Address receiver : receivers )
                        {
                            try
                            {
                                channel.send( receiver, byteBuffer );
                            }
                            catch ( Exception e )
                            {
                                throw new LightningClusterException(
                                                                     "Could not send ClassDefinitionContainer to address "
                                                                         + receiver, e );
                            }
                        }
                    }
                };
            }
            catch ( Exception e )
            {
                throw new LightningClusterException( "Could not serialize ClassDefinitionContainer", e );
            }
        }

        return null;
    }

    private Runnable handleSplitBrainMerge( View view )
    {
        final List<Address> members = new ArrayList<Address>( view.getMembers() );

        // Quote from JGroups documentation:
        // *Note that the first member of a view is the coordinator (the one who
        // emits new views).*
        Address coordinator = members.get( 0 );

        if ( channel.getAddress().equals( coordinator ) )
        {
            final ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

            try
            {
                final byte[] byteBuffer = Util.objectToByteBuffer( container );

                return new Runnable()
                {

                    @Override
                    public void run()
                    {
                        for ( int i = 1; i < members.size(); i++ )
                        {
                            Address receiver = members.get( i );
                            if ( receiver == null )
                            {
                                continue;
                            }

                            try
                            {
                                channel.send( receiver, byteBuffer );
                            }
                            catch ( Exception e )
                            {
                                throw new LightningClusterException(
                                                                     "Could not send ClassDefinitionContainer to address "
                                                                         + receiver, e );
                            }
                        }
                    }
                };
            }
            catch ( Exception e )
            {
                throw new LightningClusterException( "Could not serialize ClassDefinitionContainer", e );
            }
        }

        return null;
    }

    private List<Address> findNewMembers( View view )
    {
        List<Address> newMembers = new ArrayList<Address>();
        for ( Address member : view.getMembers() )
        {
            if ( !lastMembersView.contains( member ) )
            {
                newMembers.add( member );
            }
        }
        return newMembers;
    }
}
