begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|threadDump
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * A tool that removes uninteresting lines from stack traces.  */
end_comment

begin_class
specifier|public
class|class
name|ThreadDumpCleaner
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|PATTERN_ARRAY
init|=
block|{
literal|"Threads class SMR info:(?s).*?\n\n"
block|,
literal|"JNI global refs.*\n\n"
block|,
literal|"\"Reference Handler\"(?s).*?\n\n"
block|,
literal|"\"C1 CompilerThread(?s).*?\n\n"
block|,
literal|"\"Concurrent Mark-Sweep GC Thread\".*\n"
block|,
literal|"\"Exception Catcher Thread\".*\n"
block|,
literal|"JNI global references:.*\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\n"
block|,
literal|"\".*?\".*\n\n"
block|,
literal|"\\$\\$YJP\\$\\$"
block|,
literal|"\"(Attach|Service|VM|GC|DestroyJavaVM|Signal|AWT|AppKit|C2 |Low Mem|"
operator|+
literal|"process reaper|YJPAgent-).*?\"(?s).*?\n\n"
block|,
literal|"   Locked ownable synchronizers:(?s).*?\n\n"
block|,
literal|"   Locked synchronizers:(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State: (TIMED_)?WAITING(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.nio.ch.KQueueArrayWrapper.kevent0(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.io.FileInputStream.readBytes(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.nio.ch.ServerSocketChannelImpl.accept(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.DualStackPlainSocketImpl.accept0(?s).*\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.nio.ch.EPollArrayWrapper.epollWait(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.nio.ch.EPoll.wait(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.lang.Object.wait(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.PlainSocketImpl.socketAccept(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.SocketInputStream.socketRead0(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.nio.ch.WindowsSelectorImpl\\$SubSelector.poll0(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.management.ThreadImpl.dumpThreads0(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at sun.misc.Unsafe.park(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.PlainSocketImpl.socketClose0(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.PlainSocketImpl.socketAvailable(?s).*?\n\n"
block|,
literal|"\".*?\".*?\n   java.lang.Thread.State:.*\n\t"
operator|+
literal|"at java.net.PlainSocketImpl.socketConnect(?s).*?\n\n"
block|,
literal|"<EndOfDump>\n\n"
block|,      }
decl_stmt|;
specifier|private
specifier|static
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
name|PATTERNS
init|=
operator|new
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|String
name|s
range|:
name|PATTERN_ARRAY
control|)
block|{
name|PATTERNS
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|File
name|process
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".txt"
argument_list|)
condition|)
block|{
name|fileName
operator|=
name|fileName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fileName
operator|.
name|length
argument_list|()
operator|-
literal|".txt"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|fileName
operator|+
literal|".filtered.txt"
argument_list|)
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|target
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|processFile
argument_list|(
name|file
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|target
return|;
block|}
specifier|private
specifier|static
name|void
name|processFile
parameter_list|(
name|File
name|file
parameter_list|,
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|LineNumberReader
name|r
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|process
argument_list|(
name|r
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|process
parameter_list|(
name|LineNumberReader
name|reader
parameter_list|,
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|activeThreadCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"Full thread dump"
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"Full Java thread dump"
argument_list|)
condition|)
block|{
if|if
condition|(
name|activeThreadCount
operator|>
literal|0
condition|)
block|{
comment|// System.out.println("Active threads: " + activeThreadCount);
block|}
name|activeThreadCount
operator|=
literal|0
expr_stmt|;
block|}
name|line
operator|=
name|removeModuleName
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|String
name|filtered
init|=
name|filter
argument_list|(
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|filtered
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|10
condition|)
block|{
name|activeThreadCount
operator|++
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|println
argument_list|(
name|filter
argument_list|(
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|removeModuleName
parameter_list|(
name|String
name|line
parameter_list|)
block|{
comment|// remove the module name, for example in:
comment|// at java.base@11.0.1/sun.nio.ch.ServerSocketChannelImpl.accept
comment|// at java.management@11.0.1/sun.management.
comment|// at platform/java.scripting@11.0.1/javax.script.SimpleBindings
name|int
name|atChar
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|atChar
operator|<
literal|0
condition|)
block|{
return|return
name|line
return|;
block|}
name|int
name|slash
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|atChar
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|<
literal|0
condition|)
block|{
return|return
name|line
return|;
block|}
name|int
name|start
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|"at "
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
argument_list|<
literal|0
operator|||
name|start
argument_list|>
name|atChar
condition|)
block|{
return|return
name|line
return|;
block|}
name|start
operator|+=
literal|2
expr_stmt|;
comment|// at java.lang.Object.wait(java.base@11.0.3/Native Method) ->
comment|// at java.lang.Object.wait(Native Method) ->
name|int
name|open
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
if|if
condition|(
name|open
operator|>
name|start
operator|&&
name|open
operator|<
name|atChar
condition|)
block|{
name|start
operator|=
name|open
operator|+
literal|1
expr_stmt|;
block|}
comment|// String moduleName = line.substring(at + 3, slash);
comment|// module names seen so far:
comment|// java.base@11.0.1
comment|// java.desktop@11.0.1
comment|// java.management@11.0.1
comment|// java.xml@11.0.1
comment|// platform/java.scripting@11.0.1
comment|// removing the module name
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|start
argument_list|)
operator|+
name|line
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|line
return|;
block|}
specifier|private
specifier|static
name|String
name|filter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|p
range|:
name|PATTERNS
control|)
block|{
name|s
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

