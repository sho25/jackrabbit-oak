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
name|segment
operator|.
name|file
package|;
end_package

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
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
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

begin_comment
comment|/**  * Thread-safe class tracking files to be removed.  */
end_comment

begin_class
class|class
name|FileReaper
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FileReaper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|File
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**      * Add files to be removed. The same file can be added more than once.      * Duplicates are ignored.      *      * @param files group of files to be removed.      */
name|void
name|add
parameter_list|(
name|Iterable
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|this
operator|.
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Reap previously added files.      */
name|void
name|reap
parameter_list|()
block|{
name|Set
argument_list|<
name|File
argument_list|>
name|reap
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|reap
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|File
argument_list|>
name|redo
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|reap
control|)
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Removed file {}"
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to remove file %s"
argument_list|,
name|file
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|redo
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|redo
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|add
argument_list|(
name|redo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

