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
name|index
operator|.
name|importer
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|collect
operator|.
name|Maps
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
name|api
operator|.
name|CommitFailedException
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
name|commons
operator|.
name|PathUtils
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
name|commons
operator|.
name|json
operator|.
name|JsopReader
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
name|commons
operator|.
name|json
operator|.
name|JsopTokenizer
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
name|json
operator|.
name|Base64BlobSerializer
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
name|json
operator|.
name|JsonDeserializer
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
name|json
operator|.
name|JsopDiff
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
name|ApplyDiff
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
name|NodeBuilder
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
name|NodeStateUtils
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
name|checkState
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
name|plugins
operator|.
name|index
operator|.
name|importer
operator|.
name|NodeStoreUtils
operator|.
name|childBuilder
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_class
specifier|public
class|class
name|IndexDefinitionUpdater
block|{
comment|/**      * Name of file which would be check for presence of index-definitions      */
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DEFINITIONS_JSON
init|=
literal|"index-definitions.json"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|indexNodeStates
decl_stmt|;
specifier|public
name|IndexDefinitionUpdater
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
name|file
operator|.
name|canRead
argument_list|()
argument_list|,
literal|"File [%s] cannot be read"
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexNodeStates
operator|=
name|getIndexDefnStates
argument_list|(
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|file
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexDefinitionUpdater
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexNodeStates
operator|=
name|getIndexDefnStates
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|apply
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|cne
range|:
name|indexNodeStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|indexPath
init|=
name|cne
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|apply
argument_list|(
name|rootBuilder
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|NodeBuilder
name|apply
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
name|String
name|indexNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|NodeState
name|newDefinition
init|=
name|indexNodeStates
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|NodeState
name|parent
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|rootBuilder
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|parentPath
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|parent
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Parent node at path [%s] not found while "
operator|+
literal|"adding new index definition for [%s]. Intermediate paths node must exist for new index "
operator|+
literal|"nodes to be created"
argument_list|,
name|parentPath
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|NodeState
name|indexDefinitionNode
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|indexNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexDefinitionNode
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating index definition at path [{}]. Changes are "
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|String
name|diff
init|=
name|JsopDiff
operator|.
name|diffToJsop
argument_list|(
name|cloneVisibleState
argument_list|(
name|indexDefinitionNode
argument_list|)
argument_list|,
name|cloneVisibleState
argument_list|(
name|newDefinition
argument_list|)
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding new index definition at path [{}]"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|indexBuilderParent
init|=
name|childBuilder
argument_list|(
name|rootBuilder
argument_list|,
name|parentPath
argument_list|)
decl_stmt|;
comment|//TODO Need to update parent :childNode list if parent has orderable children
name|indexBuilderParent
operator|.
name|setChildNode
argument_list|(
name|indexNodeName
argument_list|,
name|newDefinition
argument_list|)
expr_stmt|;
return|return
name|indexBuilderParent
operator|.
name|getChildNode
argument_list|(
name|indexNodeName
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getIndexPaths
parameter_list|()
block|{
return|return
name|indexNodeStates
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|NodeState
name|getIndexState
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
return|return
name|indexNodeStates
operator|.
name|getOrDefault
argument_list|(
name|indexPath
argument_list|,
name|EMPTY_NODE
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|getIndexDefnStates
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|Base64BlobSerializer
name|blobHandler
init|=
operator|new
name|Base64BlobSerializer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|indexDefns
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|String
name|indexPath
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|indexPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid format of index definitions. The key name [%s] should "
operator|+
literal|"be index path "
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|JsonDeserializer
name|deserializer
init|=
operator|new
name|JsonDeserializer
argument_list|(
name|blobHandler
argument_list|)
decl_stmt|;
name|NodeState
name|idxState
init|=
name|deserializer
operator|.
name|deserialize
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|indexDefns
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|idxState
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
return|return
name|indexDefns
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|cloneVisibleState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|ApplyVisibleDiff
argument_list|(
name|builder
argument_list|)
operator|.
name|apply
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|ApplyVisibleDiff
extends|extends
name|ApplyDiff
block|{
specifier|public
name|ApplyVisibleDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|ApplyVisibleDiff
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

