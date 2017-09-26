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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|SegmentNodeBuilder
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
name|file
operator|.
name|FileStore
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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|JcrConstants
operator|.
name|JCR_UUID
import|;
end_import

begin_class
specifier|public
class|class
name|CompositePrepareCommand
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
name|?
argument_list|>
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"paths"
argument_list|,
literal|"a list of paths to transform from nt:resource to oak:Resource"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|withValuesSeparatedBy
argument_list|(
literal|','
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"/apps"
argument_list|,
literal|"/libs"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeO
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"path to segment store (required)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
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
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|File
name|store
init|=
name|storeO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
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
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|FileStore
name|fs
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
operator|new
name|OakResourceTransformer
argument_list|(
name|fs
argument_list|,
name|paths
operator|.
name|values
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|transform
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|OakResourceTransformer
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CompositePrepareCommand
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
specifier|private
name|int
name|totalNodes
decl_stmt|;
specifier|private
name|int
name|modifiedNodes
decl_stmt|;
specifier|public
name|OakResourceTransformer
parameter_list|(
name|FileStore
name|fileStore
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|fileStore
operator|=
name|fileStore
expr_stmt|;
block|}
specifier|public
name|void
name|transform
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|SegmentNodeState
name|headNodeState
init|=
name|fileStore
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|superRootBuilder
init|=
name|headNodeState
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|cp
range|:
name|superRootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Transforming checkpoint {}"
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|NodeBuilder
name|cpRoot
init|=
name|superRootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|cp
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|transformRootBuilder
argument_list|(
name|cpRoot
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Transforming root"
argument_list|)
expr_stmt|;
name|transformRootBuilder
argument_list|(
name|superRootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|headNodeState
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|superRootBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|transformRootBuilder
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|)
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|NodeBuilder
name|builder
init|=
name|rootBuilder
decl_stmt|;
for|for
control|(
name|String
name|e
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|p
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|totalNodes
operator|=
name|modifiedNodes
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"  path: {}"
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|NodeBuilder
name|indexData
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"uuid"
argument_list|)
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|transformBuilder
argument_list|(
name|builder
argument_list|,
name|indexData
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"    all nodes: {}, updated nodes: {}"
argument_list|,
name|totalNodes
argument_list|,
name|modifiedNodes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|transformBuilder
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|NodeBuilder
name|uuidIndexData
parameter_list|)
block|{
name|String
name|type
init|=
name|builder
operator|.
name|getName
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|NodeTypeConstants
operator|.
name|NT_RESOURCE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|String
name|index
init|=
name|builder
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
name|uuidIndexData
operator|.
name|getChildNode
argument_list|(
name|index
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_RESOURCE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
name|JCR_UUID
argument_list|)
expr_stmt|;
name|modifiedNodes
operator|++
expr_stmt|;
block|}
for|for
control|(
name|String
name|child
range|:
name|builder
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|transformBuilder
argument_list|(
name|builder
operator|.
name|getChildNode
argument_list|(
name|child
argument_list|)
argument_list|,
name|uuidIndexData
argument_list|)
expr_stmt|;
block|}
name|totalNodes
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
