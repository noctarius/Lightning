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
package org.apache.directmemory.lightning.internal.util;

import java.io.PrintStream;

import org.apache.directmemory.lightning.logging.LogLevel;
import org.apache.directmemory.lightning.logging.LoggerAdapter;

public class DebugLogger
    extends LoggerAdapter
{

    @Override
    public boolean isLogLevelEnabled( LogLevel logLevel )
    {
        return true;
    }

    @Override
    public boolean isTraceEnabled()
    {
        return true;
    }

    @Override
    public boolean isDebugEnabled()
    {
        return true;
    }

    @Override
    public boolean isInfoEnabled()
    {
        return true;
    }

    @Override
    public boolean isWarnEnabled()
    {
        return true;
    }

    @Override
    public boolean isErrorEnabled()
    {
        return true;
    }

    @Override
    public boolean isFatalEnabled()
    {
        return true;
    }

    @Override
    public void trace( String message )
    {
        log( LogLevel.Trace, message, null );
    }

    @Override
    public void trace( String message, Throwable throwable )
    {
        log( LogLevel.Trace, message, throwable );
    }

    @Override
    public void debug( String message )
    {
        log( LogLevel.Debug, message, null );
    }

    @Override
    public void debug( String message, Throwable throwable )
    {
        log( LogLevel.Debug, message, throwable );
    }

    @Override
    public void info( String message )
    {
        log( LogLevel.Info, message, null );
    }

    @Override
    public void info( String message, Throwable throwable )
    {
        log( LogLevel.Info, message, throwable );
    }

    @Override
    public void warn( String message )
    {
        log( LogLevel.Warn, message, null );
    }

    @Override
    public void warn( String message, Throwable throwable )
    {
        log( LogLevel.Warn, message, throwable );
    }

    @Override
    public void error( String message )
    {
        log( LogLevel.Error, message, null );
    }

    @Override
    public void error( String message, Throwable throwable )
    {
        log( LogLevel.Error, message, throwable );
    }

    @Override
    public void fatal( String message )
    {
        log( LogLevel.Fatal, message, null );
    }

    @Override
    public void fatal( String message, Throwable throwable )
    {
        log( LogLevel.Fatal, message, throwable );
    }

    private void log( LogLevel logLevel, String message, Throwable throwable )
    {
        PrintStream stream;
        if ( throwable != null )
        {
            stream = System.err;
        }
        else
        {
            stream = System.out;
        }

        stream.println( getName() + " - " + logLevel.name() + ": " + message );
        if ( throwable != null )
        {
            throwable.printStackTrace();
        }
    }
}
