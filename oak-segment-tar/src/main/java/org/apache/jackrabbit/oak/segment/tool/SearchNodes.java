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
name|segment
operator|.
name|tool
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
name|Utils
operator|.
name|parseSegmentInfoTimestamp
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
name|PrintStream
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
name|HashSet
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Type
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
name|RecordId
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
name|RecordType
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
name|SegmentId
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
name|SegmentNodeState
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
name|SegmentNotFoundException
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
name|file
operator|.
name|FileStoreBuilder
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
name|file
operator|.
name|ReadOnlyFileStore
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|SearchNodes
block|{
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
specifier|private
interface|interface
name|Matcher
block|{
name|boolean
name|matches
parameter_list|(
name|NodeState
name|node
parameter_list|)
function_decl|;
block|}
specifier|public
enum|enum
name|Output
block|{
name|TEXT
block|,
name|JOURNAL
block|}
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|path
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Matcher
argument_list|>
name|matchers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Output
name|output
init|=
name|Output
operator|.
name|TEXT
decl_stmt|;
specifier|private
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
specifier|private
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|add
argument_list|(
name|node
lambda|->
name|node
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|add
argument_list|(
name|node
lambda|->
name|node
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withValue
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|matchers
operator|.
name|add
argument_list|(
name|node
lambda|->
block|{
name|PropertyState
name|p
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|v
range|:
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
return|return
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
name|value
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withOutput
parameter_list|(
name|Output
name|output
parameter_list|)
block|{
name|this
operator|.
name|output
operator|=
name|checkNotNull
argument_list|(
name|output
argument_list|,
literal|"output"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withOut
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|checkNotNull
argument_list|(
name|out
argument_list|,
literal|"out"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withErr
parameter_list|(
name|PrintStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|err
operator|=
name|checkNotNull
argument_list|(
name|err
argument_list|,
literal|"err"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SearchNodes
name|build
parameter_list|()
block|{
name|checkArgument
argument_list|(
name|path
operator|!=
literal|null
argument_list|,
literal|"path not specified"
argument_list|)
expr_stmt|;
return|return
operator|new
name|SearchNodes
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|>
name|matchers
decl_stmt|;
specifier|private
specifier|final
name|Output
name|output
decl_stmt|;
specifier|private
specifier|final
name|PrintStream
name|out
decl_stmt|;
specifier|private
specifier|final
name|PrintStream
name|err
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|notFoundSegments
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|SearchNodes
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|matchers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|builder
operator|.
name|matchers
argument_list|)
expr_stmt|;
name|this
operator|.
name|output
operator|=
name|builder
operator|.
name|output
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|builder
operator|.
name|out
expr_stmt|;
name|this
operator|.
name|err
operator|=
name|builder
operator|.
name|err
expr_stmt|;
block|}
specifier|public
name|int
name|run
parameter_list|()
block|{
try|try
init|(
name|ReadOnlyFileStore
name|fileStore
init|=
name|newFileStore
argument_list|()
init|)
block|{
for|for
control|(
name|SegmentId
name|segmentId
range|:
name|fileStore
operator|.
name|getSegmentIds
argument_list|()
control|)
block|{
try|try
block|{
name|processSegment
argument_list|(
name|fileStore
argument_list|,
name|segmentId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|handle
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|private
name|ReadOnlyFileStore
name|newFileStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|path
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
return|;
block|}
specifier|private
name|void
name|processSegment
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|,
name|SegmentId
name|segmentId
parameter_list|)
block|{
if|if
condition|(
name|segmentId
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
return|return;
block|}
name|Long
name|timestamp
init|=
name|parseSegmentInfoTimestamp
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|timestamp
operator|==
literal|null
condition|)
block|{
name|err
operator|.
name|printf
argument_list|(
literal|"No timestamp found in segment %s\n"
argument_list|,
name|segmentId
argument_list|)
expr_stmt|;
return|return;
block|}
name|segmentId
operator|.
name|getSegment
argument_list|()
operator|.
name|forEachRecord
argument_list|(
parameter_list|(
name|number
parameter_list|,
name|type
parameter_list|,
name|offset
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|type
operator|!=
name|RecordType
operator|.
name|NODE
condition|)
block|{
return|return;
block|}
try|try
block|{
name|processRecord
argument_list|(
name|fileStore
argument_list|,
name|timestamp
argument_list|,
operator|new
name|RecordId
argument_list|(
name|segmentId
argument_list|,
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|handle
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|processRecord
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
name|SegmentNodeState
name|nodeState
init|=
name|fileStore
operator|.
name|getReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|recordId
argument_list|)
decl_stmt|;
name|boolean
name|matches
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Matcher
name|matcher
range|:
name|matchers
control|)
block|{
name|matches
operator|=
name|matches
operator|&&
name|matcher
operator|.
name|matches
argument_list|(
name|nodeState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|matches
condition|)
block|{
return|return;
block|}
switch|switch
condition|(
name|output
condition|)
block|{
case|case
name|TEXT
case|:
name|out
operator|.
name|printf
argument_list|(
literal|"%d\t%s\n"
argument_list|,
name|timestamp
argument_list|,
name|recordId
argument_list|)
expr_stmt|;
break|break;
case|case
name|JOURNAL
case|:
name|out
operator|.
name|printf
argument_list|(
literal|"%s root %d\n"
argument_list|,
name|recordId
operator|.
name|toString10
argument_list|()
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unrecognized output"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|handle
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|notFoundSegments
operator|.
name|add
argument_list|(
name|e
operator|.
name|getSegmentId
argument_list|()
argument_list|)
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

