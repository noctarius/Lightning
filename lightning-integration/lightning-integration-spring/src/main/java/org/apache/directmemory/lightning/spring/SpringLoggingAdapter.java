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
package org.apache.directmemory.lightning.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directmemory.lightning.logging.LogLevel;
import org.apache.directmemory.lightning.logging.Logger;

public class SpringLoggingAdapter
    implements Logger
{

    private final Log logger;

    private final String name;

    public SpringLoggingAdapter()
    {
        this( SpringLoggingAdapter.class );
    }

    public SpringLoggingAdapter( Class<?> clazz )
    {
        this.logger = LogFactory.getLog( clazz );
        this.name = clazz.getCanonicalName();
    }

    public SpringLoggingAdapter( String name )
    {
        this.logger = LogFactory.getLog( name );
        this.name = name;
    }

    @Override
    public Logger getChildLogger( Class<?> clazz )
    {
        return new SpringLoggingAdapter( clazz );
    }

    @Override
    public Logger getChildLogger( String name )
    {
        return new SpringLoggingAdapter( name );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isLogLevelEnabled( LogLevel logLevel )
    {
        switch ( logLevel )
        {
            case Debug:
                return isDebugEnabled();
            case Error:
                return isErrorEnabled();
            case Fatal:
                return isFatalEnabled();
            case Info:
                return isInfoEnabled();
            case Trace:
                return isTraceEnabled();
            case Warn:
                return isWarnEnabled();
        }

        return false;
    }

    @Override
    public boolean isTraceEnabled()
    {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled()
    {
        return logger.isFatalEnabled();
    }

    @Override
    public void trace( String message )
    {
        trace( message, null );
    }

    @Override
    public void trace( String message, Throwable throwable )
    {
        logger.trace( message, throwable );
    }

    @Override
    public void debug( String message )
    {
        debug( message, null );
    }

    @Override
    public void debug( String message, Throwable throwable )
    {
        logger.debug( message, throwable );
    }

    @Override
    public void info( String message )
    {
        info( message, null );
    }

    @Override
    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    @Override
    public void warn( String message )
    {
        warn( message, null );
    }

    @Override
    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

    @Override
    public void error( String message )
    {
        error( message, null );
    }

    @Override
    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    @Override
    public void fatal( String message )
    {
        fatal( message, null );
    }

    @Override
    public void fatal( String message, Throwable throwable )
    {
        logger.fatal( message, throwable );
    }
}
