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
name|run
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
name|List
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|segment
operator|.
name|tool
operator|.
name|DebugSegments
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
name|segment
operator|.
name|tool
operator|.
name|DebugStore
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
name|segment
operator|.
name|tool
operator|.
name|DebugTars
import|;
end_import

begin_class
class|class
name|DebugCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|nonOptions
init|=
name|parser
operator|.
name|nonOptions
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|valuesOf
argument_list|(
name|nonOptions
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"usage: debug<path> [id...]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|debug
argument_list|(
name|options
operator|.
name|valuesOf
argument_list|(
name|nonOptions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|int
name|debug
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tars
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|segs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
condition|)
block|{
name|tars
operator|.
name|add
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|segs
operator|.
name|add
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|returnCode
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|tars
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|debugTars
argument_list|(
name|file
argument_list|,
name|tars
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|returnCode
operator|=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|segs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|debugSegments
argument_list|(
name|file
argument_list|,
name|segs
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|returnCode
operator|=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tars
operator|.
name|isEmpty
argument_list|()
operator|&&
name|segs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|debugStore
argument_list|(
name|file
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|returnCode
operator|=
literal|1
expr_stmt|;
block|}
block|}
return|return
name|returnCode
return|;
block|}
specifier|private
specifier|static
name|int
name|debugTars
parameter_list|(
name|File
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|tars
parameter_list|)
block|{
name|DebugTars
operator|.
name|Builder
name|builder
init|=
name|DebugTars
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tar
range|:
name|tars
control|)
block|{
name|builder
operator|.
name|withTar
argument_list|(
name|tar
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|int
name|debugSegments
parameter_list|(
name|File
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|segments
parameter_list|)
block|{
name|DebugSegments
operator|.
name|Builder
name|builder
init|=
name|DebugSegments
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|segments
control|)
block|{
name|builder
operator|.
name|withSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|int
name|debugStore
parameter_list|(
name|File
name|store
parameter_list|)
block|{
return|return
name|DebugStore
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
return|;
block|}
block|}
end_class

end_unit

