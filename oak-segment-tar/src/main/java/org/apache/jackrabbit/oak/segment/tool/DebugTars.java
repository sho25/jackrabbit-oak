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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newTreeSet
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
name|SegmentNodeStateHelper
operator|.
name|getTemplateId
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
name|openReadOnlyFileStore
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|escape
operator|.
name|Escapers
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
name|Blob
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
name|SegmentBlob
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
name|SegmentPropertyState
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
name|ChildNodeEntry
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

begin_comment
comment|/**  * Print information about one or more TAR files from an existing segment store.  */
end_comment

begin_class
specifier|public
class|class
name|DebugTars
implements|implements
name|Runnable
block|{
comment|/**      * Create a builder for the {@link DebugTars} command.      *      * @return an instance of {@link Builder}.      */
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
comment|/**      * Collect options for the {@link DebugTars} command.      */
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
specifier|final
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
specifier|private
specifier|final
name|int
name|maxCharDisplay
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"max.char.display"
argument_list|,
literal|60
argument_list|)
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The path to an existing segment store. This parameter is required.          *          * @param path the path to an existing segment store.          * @return this builder.          */
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
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Add a TAR file. The command will print information about every TAR          * file added via this method. It is mandatory to add at least one TAR          * file.          *          * @param tar the name of a TAR file.          * @return this builder.          */
specifier|public
name|Builder
name|withTar
parameter_list|(
name|String
name|tar
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|tar
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tars
operator|.
name|add
argument_list|(
name|tar
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link DebugTars} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|Runnable
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|tars
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|DebugTars
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
name|String
argument_list|>
name|tars
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxCharDisplay
decl_stmt|;
specifier|private
name|DebugTars
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
name|tars
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|builder
operator|.
name|tars
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxCharDisplay
operator|=
name|builder
operator|.
name|maxCharDisplay
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|ReadOnlyFileStore
name|store
init|=
name|openReadOnlyFileStore
argument_list|(
name|path
argument_list|)
init|)
block|{
name|debugTarFiles
argument_list|(
name|store
argument_list|)
expr_stmt|;
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
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|debugTarFiles
parameter_list|(
name|ReadOnlyFileStore
name|store
parameter_list|)
block|{
for|for
control|(
name|String
name|tar
range|:
name|tars
control|)
block|{
name|debugTarFile
argument_list|(
name|store
argument_list|,
name|tar
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|debugTarFile
parameter_list|(
name|ReadOnlyFileStore
name|store
parameter_list|,
name|String
name|t
parameter_list|)
block|{
name|File
name|tar
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tar
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"file doesn't exist, skipping "
operator|+
name|t
argument_list|)
expr_stmt|;
return|return;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Debug file "
operator|+
name|tar
operator|+
literal|"("
operator|+
name|tar
operator|.
name|length
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
operator|new
name|HashSet
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|hasRefs
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|e
range|:
name|store
operator|.
name|getTarReaderIndex
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|endsWith
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|hasRefs
operator|=
literal|true
expr_stmt|;
name|uuids
operator|=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasRefs
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SegmentNodeState references to "
operator|+
name|t
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|filterNodeStates
argument_list|(
name|uuids
argument_list|,
name|paths
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|p
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No references to "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
name|store
operator|.
name|getTarGraph
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Tar graph:"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|entry
range|:
name|graph
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|'='
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error getting tar graph:"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|filterNodeStates
parameter_list|(
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|SegmentNodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|localPaths
init|=
name|newTreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|ps
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
name|SegmentPropertyState
name|sps
init|=
operator|(
name|SegmentPropertyState
operator|)
name|ps
decl_stmt|;
name|RecordId
name|recordId
init|=
name|sps
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|UUID
name|id
init|=
name|recordId
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
decl_stmt|;
if|if
condition|(
name|uuids
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|STRING
condition|)
block|{
name|String
name|val
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// only shows the first value, do we need more?
name|val
operator|=
name|displayString
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|localPaths
operator|.
name|add
argument_list|(
name|getLocalPath
argument_list|(
name|path
argument_list|,
name|ps
argument_list|,
name|val
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|localPaths
operator|.
name|add
argument_list|(
name|getLocalPath
argument_list|(
name|path
argument_list|,
name|ps
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
comment|// look for extra segment references
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ps
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|SegmentId
name|sbid
range|:
name|SegmentBlob
operator|.
name|getBulkSegmentIds
argument_list|(
name|b
argument_list|)
control|)
block|{
name|UUID
name|bid
init|=
name|sbid
operator|.
name|asUUID
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|bid
operator|.
name|equals
argument_list|(
name|id
argument_list|)
operator|&&
name|uuids
operator|.
name|contains
argument_list|(
name|bid
argument_list|)
condition|)
block|{
name|localPaths
operator|.
name|add
argument_list|(
name|getLocalPath
argument_list|(
name|path
argument_list|,
name|ps
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
name|RecordId
name|stateId
init|=
name|state
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
if|if
condition|(
name|uuids
operator|.
name|contains
argument_list|(
name|stateId
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
argument_list|)
condition|)
block|{
name|localPaths
operator|.
name|add
argument_list|(
name|path
operator|+
literal|" [SegmentNodeState@"
operator|+
name|stateId
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|RecordId
name|templateId
init|=
name|getTemplateId
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuids
operator|.
name|contains
argument_list|(
name|templateId
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
argument_list|)
condition|)
block|{
name|localPaths
operator|.
name|add
argument_list|(
name|path
operator|+
literal|"[Template@"
operator|+
name|templateId
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|paths
operator|.
name|addAll
argument_list|(
name|localPaths
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|ce
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|c
init|=
name|ce
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|filterNodeStates
argument_list|(
name|uuids
argument_list|,
name|paths
argument_list|,
operator|(
name|SegmentNodeState
operator|)
name|c
argument_list|,
name|path
operator|+
name|ce
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|getLocalPath
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|ps
parameter_list|,
name|String
name|value
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|path
operator|+
name|ps
operator|.
name|getName
argument_list|()
operator|+
literal|" = "
operator|+
name|value
operator|+
literal|" [SegmentPropertyState<"
operator|+
name|ps
operator|.
name|getType
argument_list|()
operator|+
literal|">@"
operator|+
name|id
operator|+
literal|"]"
return|;
block|}
specifier|private
specifier|static
name|String
name|getLocalPath
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|ps
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|path
operator|+
name|ps
operator|+
literal|" [SegmentPropertyState<"
operator|+
name|ps
operator|.
name|getType
argument_list|()
operator|+
literal|">@"
operator|+
name|id
operator|+
literal|"]"
return|;
block|}
specifier|private
name|String
name|displayString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|maxCharDisplay
operator|>
literal|0
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
name|maxCharDisplay
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxCharDisplay
argument_list|)
operator|+
literal|"... ("
operator|+
name|value
operator|.
name|length
argument_list|()
operator|+
literal|" chars)"
expr_stmt|;
block|}
name|String
name|escaped
init|=
name|Escapers
operator|.
name|builder
argument_list|()
operator|.
name|setSafeRange
argument_list|(
literal|' '
argument_list|,
literal|'~'
argument_list|)
operator|.
name|addEscape
argument_list|(
literal|'"'
argument_list|,
literal|"\\\""
argument_list|)
operator|.
name|addEscape
argument_list|(
literal|'\\'
argument_list|,
literal|"\\\\"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|escape
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
literal|'"'
operator|+
name|escaped
operator|+
literal|'"'
return|;
block|}
block|}
end_class

end_unit

