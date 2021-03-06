begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|persistentCache
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|cache
operator|.
name|CacheLIRS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|MemoryDiffCache
operator|.
name|Key
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|RevisionVector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|persistentCache
operator|.
name|broadcast
operator|.
name|Broadcaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|persistentCache
operator|.
name|broadcast
operator|.
name|TCPBroadcaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|LoggerContext
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|PatternLayout
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|spi
operator|.
name|ILoggingEvent
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|ConsoleAppender
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_class
specifier|public
class|class
name|BroadcastTest
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|listen
argument_list|()
expr_stmt|;
name|benchmark
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|benchmark
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/broadcastTest"
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/broadcastTest"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|String
name|type
init|=
literal|"tcp:key 1;ports 9700 9800"
decl_stmt|;
name|ArrayList
argument_list|<
name|PersistentCache
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<
name|PersistentCache
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|nodes
init|=
literal|1
init|;
name|nodes
operator|<
literal|20
condition|;
name|nodes
operator|++
control|)
block|{
name|PersistentCache
name|pc
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/broadcastTest/p"
operator|+
name|nodes
operator|+
literal|",broadcast="
operator|+
name|type
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
name|cache
init|=
name|openCache
argument_list|(
name|pc
argument_list|)
decl_stmt|;
name|Path
name|key
init|=
name|Path
operator|.
name|fromString
argument_list|(
literal|"/test"
operator|+
name|Math
operator|.
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|RevisionVector
name|from
init|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
decl_stmt|;
name|RevisionVector
name|to
init|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
literal|"r2-0-1"
argument_list|)
decl_stmt|;
name|Key
name|k
init|=
operator|new
name|Key
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Hello World "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|invalidate
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nodes: "
operator|+
name|nodes
operator|+
literal|" time: "
operator|+
name|time
argument_list|)
expr_stmt|;
name|nodeList
operator|.
name|add
argument_list|(
name|pc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PersistentCache
name|c
range|:
name|nodeList
control|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|listen
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|String
name|config
init|=
literal|"key 123"
decl_stmt|;
name|ConsoleAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|ca
init|=
operator|new
name|ConsoleAppender
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|()
decl_stmt|;
name|LoggerContext
name|lc
init|=
operator|(
name|LoggerContext
operator|)
name|LoggerFactory
operator|.
name|getILoggerFactory
argument_list|()
decl_stmt|;
name|ca
operator|.
name|setContext
argument_list|(
name|lc
argument_list|)
expr_stmt|;
name|PatternLayout
name|pl
init|=
operator|new
name|PatternLayout
argument_list|()
decl_stmt|;
name|pl
operator|.
name|setPattern
argument_list|(
literal|"%msg%n"
argument_list|)
expr_stmt|;
name|pl
operator|.
name|setContext
argument_list|(
name|lc
argument_list|)
expr_stmt|;
name|pl
operator|.
name|start
argument_list|()
expr_stmt|;
name|ca
operator|.
name|setLayout
argument_list|(
name|pl
argument_list|)
expr_stmt|;
name|ca
operator|.
name|start
argument_list|()
expr_stmt|;
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
name|logger
init|=
operator|(
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
operator|)
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TCPBroadcaster
operator|.
name|class
argument_list|)
decl_stmt|;
name|logger
operator|.
name|addAppender
argument_list|(
name|ca
argument_list|)
expr_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|TCPBroadcaster
name|receiver
init|=
operator|new
name|TCPBroadcaster
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|addListener
argument_list|(
operator|new
name|Broadcaster
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
block|{
name|int
name|end
init|=
name|buff
operator|.
name|position
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|buff
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|char
name|c
init|=
operator|(
name|char
operator|)
name|buff
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|' '
operator|&&
name|c
operator|<
literal|128
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|<=
literal|9
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'0'
operator|+
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|dateTime
init|=
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|19
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dateTime
operator|+
literal|" Received "
operator|+
name|sb
argument_list|)
expr_stmt|;
name|buff
operator|.
name|position
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|x
init|=
name|r
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending "
operator|+
name|x
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buff
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|buff
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buff
operator|.
name|putInt
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|buff
operator|.
name|put
argument_list|(
operator|new
name|byte
index|[
literal|100
index|]
argument_list|)
expr_stmt|;
name|buff
operator|.
name|flip
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|send
argument_list|(
name|buff
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|receiver
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Did not start or already stopped"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|broadcastTCP
parameter_list|()
throws|throws
name|Exception
block|{
name|broadcast
argument_list|(
literal|"tcp:sendTo localhost;key 123"
argument_list|,
literal|80
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|broadcastInMemory
parameter_list|()
throws|throws
name|Exception
block|{
name|broadcast
argument_list|(
literal|"inMemory"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2843"
argument_list|)
specifier|public
name|void
name|broadcastUDP
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|broadcast
argument_list|(
literal|"udp:sendTo localhost"
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// IPv6 didn't work, so try with IPv4
try|try
block|{
name|broadcast
argument_list|(
literal|"udp:group 228.6.7.9"
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e2
parameter_list|)
block|{
name|throwBoth
argument_list|(
name|e
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2843"
argument_list|)
specifier|public
name|void
name|broadcastEncryptedUDP
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|broadcast
argument_list|(
literal|"udp:group FF78:230::1234;key test;port 9876;sendTo localhost;aes"
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
try|try
block|{
name|broadcast
argument_list|(
literal|"udp:group 228.6.7.9;key test;port 9876;aes"
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e2
parameter_list|)
block|{
name|throwBoth
argument_list|(
name|e
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|throwBoth
parameter_list|(
name|AssertionError
name|e
parameter_list|,
name|AssertionError
name|e2
parameter_list|)
throws|throws
name|AssertionError
block|{
name|Throwable
name|ex
init|=
name|e
decl_stmt|;
while|while
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ex
operator|=
name|ex
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|ex
operator|.
name|initCause
argument_list|(
name|e2
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
specifier|private
specifier|static
name|void
name|broadcast
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|minPercentCorrect
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|broadcastTry
argument_list|(
name|type
argument_list|,
name|minPercentCorrect
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|broadcastTry
argument_list|(
name|type
argument_list|,
name|minPercentCorrect
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|broadcastTry
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|minPercentCorrect
parameter_list|,
name|boolean
name|tryOnly
parameter_list|)
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/broadcastTest"
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/broadcastTest"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|PersistentCache
name|p1
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/broadcastTest/p1,broadcast="
operator|+
name|type
argument_list|)
decl_stmt|;
name|PersistentCache
name|p2
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/broadcastTest/p2,broadcast="
operator|+
name|type
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
name|c1
init|=
name|openCache
argument_list|(
name|p1
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
name|c2
init|=
name|openCache
argument_list|(
name|p2
argument_list|)
decl_stmt|;
name|Path
name|key
init|=
name|Path
operator|.
name|fromString
argument_list|(
literal|"/test"
operator|+
name|Math
operator|.
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|RevisionVector
name|from
init|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
decl_stmt|;
name|RevisionVector
name|to
init|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
literal|"r2-0-1"
argument_list|)
decl_stmt|;
name|Key
name|k
init|=
operator|new
name|Key
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|int
name|correct
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|c1
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Hello World "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|c2
argument_list|,
name|k
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|StringValue
name|v2
init|=
name|c2
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|v2
operator|!=
literal|null
operator|&&
name|v2
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Hello World "
operator|+
name|i
argument_list|)
condition|)
block|{
name|correct
operator|++
expr_stmt|;
block|}
name|c2
operator|.
name|invalidate
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c2
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|c1
argument_list|,
name|k
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|StringValue
name|v1
init|=
name|c1
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1
operator|==
literal|null
condition|)
block|{
name|correct
operator|++
expr_stmt|;
block|}
block|}
name|p1
operator|.
name|close
argument_list|()
expr_stmt|;
name|p2
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|correct
operator|>=
name|minPercentCorrect
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|tryOnly
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Assert
operator|.
name|fail
argument_list|(
literal|"min: "
operator|+
name|minPercentCorrect
operator|+
literal|" got: "
operator|+
name|correct
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|waitFor
parameter_list|(
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|call
parameter_list|,
name|int
name|timeoutInMilliseconds
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
comment|// ignore
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
try|try
block|{
if|if
condition|(
name|call
operator|.
name|call
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|time
operator|>
name|timeoutInMilliseconds
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|boolean
name|waitFor
parameter_list|(
specifier|final
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|,
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|,
name|int
name|timeoutInMilliseconds
parameter_list|)
block|{
return|return
name|waitFor
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
block|{
name|V
name|v
init|=
name|map
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|v
operator|==
literal|null
return|;
block|}
return|return
name|value
operator|.
name|equals
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
argument_list|,
name|timeoutInMilliseconds
argument_list|)
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|boolean
name|waitFor
parameter_list|(
specifier|final
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|,
specifier|final
name|K
name|key
parameter_list|,
name|int
name|timeoutInMilliseconds
parameter_list|)
block|{
return|return
name|waitFor
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|map
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|timeoutInMilliseconds
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Cache
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
name|openCache
parameter_list|(
name|PersistentCache
name|p
parameter_list|)
block|{
name|CacheLIRS
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|<
name|Key
argument_list|,
name|StringValue
argument_list|>
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|p
operator|.
name|wrap
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|cache
argument_list|,
name|CacheType
operator|.
name|DIFF
argument_list|)
return|;
block|}
block|}
end_class

end_unit

