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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|StringReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Queue
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
name|LinkedBlockingDeque
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
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
name|Joiner
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
name|EvictingQueue
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
name|JcrConstants
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
name|commons
operator|.
name|cnd
operator|.
name|CndImporter
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
name|commons
operator|.
name|cnd
operator|.
name|ParseException
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
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|spi
operator|.
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|JCR_SYSTEM
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
name|NT_FOLDER
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
name|NT_RESOURCE
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
name|commons
operator|.
name|JcrUtils
operator|.
name|getOrAddNode
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|BUNDLOR
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|DOCUMENT_NODE_STORE
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
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
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
name|PropertyStates
operator|.
name|createProperty
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_RESOURCE
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
import|;
end_import

begin_class
specifier|public
class|class
name|BundlingNodeTest
extends|extends
name|AbstractTest
argument_list|<
name|BundlingNodeTest
operator|.
name|TestContext
argument_list|>
block|{
enum|enum
name|Status
block|{
name|NONE
block|,
name|STARTING
block|,
name|STARTED
block|,
name|STOPPING
block|,
name|STOPPED
block|,
name|ABORTED
block|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|public
name|void
name|inc
parameter_list|()
block|{
name|count
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
specifier|public
name|Status
name|next
parameter_list|()
block|{
name|Status
index|[]
name|ss
init|=
name|values
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
argument_list|()
operator|==
name|ss
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
name|ss
index|[
literal|0
index|]
return|;
block|}
return|return
name|ss
index|[
name|ordinal
argument_list|()
operator|+
literal|1
index|]
return|;
block|}
block|}
specifier|private
enum|enum
name|BundlingMode
block|{
name|ALL
block|,
name|EXCLUDE_RENDITIONS
block|}
specifier|private
specifier|static
specifier|final
name|String
name|NT_OAK_ASSET
init|=
literal|"oak:Asset"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ASSET_NODE_TYPE_DEFN
init|=
literal|"[oak:Asset]\n"
operator|+
literal|" - * (UNDEFINED) multiple\n"
operator|+
literal|" - * (UNDEFINED)\n"
operator|+
literal|" + * (nt:base) = oak:Asset VERSION"
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|42
argument_list|)
decl_stmt|;
comment|//fixed seed
specifier|private
name|int
name|nodesPerFolder
init|=
literal|10
decl_stmt|;
specifier|private
name|boolean
name|bundlingEnabled
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"bundlingEnabled"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|oakResourceEnabled
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"oakResourceEnabled"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|readerEnabled
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"readerEnabled"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|BundlingMode
name|bundlingMode
init|=
name|BundlingMode
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"bundlingMode"
argument_list|,
literal|"all"
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|RepositoryInitializer
name|bundlingInitializer
init|=
operator|new
name|BundlingConfigInitializer
argument_list|()
decl_stmt|;
specifier|private
name|TestContext
name|defaultContext
decl_stmt|;
specifier|private
name|Reader
name|reader
decl_stmt|;
specifier|private
name|Mutator
name|mutator
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|assetCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|TestContext
argument_list|>
name|contexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|contentNodeType
init|=
name|NT_RESOURCE
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundlingEnabled
condition|)
block|{
name|jcr
operator|.
name|with
argument_list|(
name|bundlingInitializer
argument_list|)
expr_stmt|;
block|}
name|jcr
operator|.
name|with
argument_list|(
name|FixNodeTypeIndexInitializer
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
return|return
name|jcr
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|oakResourceEnabled
condition|)
block|{
name|contentNodeType
operator|=
name|NT_OAK_RESOURCE
expr_stmt|;
block|}
name|registerAssetNodeType
argument_list|()
expr_stmt|;
name|defaultContext
operator|=
operator|new
name|TestContext
argument_list|()
expr_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|defaultContext
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|Reader
argument_list|()
expr_stmt|;
name|mutator
operator|=
operator|new
name|Mutator
argument_list|()
expr_stmt|;
if|if
condition|(
name|readerEnabled
condition|)
block|{
name|addBackgroundJob
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|addBackgroundJob
argument_list|(
name|mutator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|TestContext
name|prepareThreadExecutionContext
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|TestContext
name|ctx
init|=
operator|new
name|TestContext
argument_list|()
decl_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
name|ctx
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|defaultContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|(
name|TestContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
comment|//Create tree in breadth first fashion with each node having 50 child
name|Node
name|parent
init|=
name|ctx
operator|.
name|session
operator|.
name|getNode
argument_list|(
name|ctx
operator|.
name|paths
operator|.
name|remove
argument_list|()
argument_list|)
decl_stmt|;
name|Status
name|status
init|=
name|Status
operator|.
name|NONE
decl_stmt|;
name|parent
operator|=
name|parent
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
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
name|nodesPerFolder
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|asset
init|=
name|parent
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
operator|+
literal|".png"
argument_list|,
name|NT_OAK_ASSET
argument_list|)
decl_stmt|;
name|createAssetNodeStructure
argument_list|(
name|asset
argument_list|,
name|status
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|ctx
operator|.
name|addAssetPath
argument_list|(
name|asset
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assetCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|status
operator|.
name|inc
argument_list|()
expr_stmt|;
name|status
operator|=
name|status
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|ctx
operator|.
name|addFolderPath
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createAssetNodeStructure
parameter_list|(
name|Node
name|asset
parameter_list|,
name|String
name|status
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|content
init|=
name|asset
operator|.
name|addNode
argument_list|(
literal|"jcr:content"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|metadata
init|=
name|content
operator|.
name|addNode
argument_list|(
literal|"metadata"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|Node
name|renditions
init|=
name|content
operator|.
name|addNode
argument_list|(
literal|"renditions"
argument_list|,
name|NT_FOLDER
argument_list|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|addFile
argument_list|(
name|renditions
argument_list|,
literal|"thumbnail-"
operator|+
name|i
operator|+
literal|".png"
argument_list|)
expr_stmt|;
block|}
name|content
operator|.
name|addNode
argument_list|(
literal|"comments"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addFile
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|file
init|=
name|getOrAddNode
argument_list|(
name|parent
argument_list|,
name|fileName
argument_list|,
name|NodeType
operator|.
name|NT_FILE
argument_list|)
decl_stmt|;
name|Node
name|content
init|=
name|getOrAddNode
argument_list|(
name|file
argument_list|,
name|Node
operator|.
name|JCR_CONTENT
argument_list|,
name|contentNodeType
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|Property
operator|.
name|JCR_MIMETYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|Property
operator|.
name|JCR_LAST_MODIFIED
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|Binary
name|binary
init|=
name|parent
operator|.
name|getSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createBinary
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|Property
operator|.
name|JCR_DATA
argument_list|,
name|binary
argument_list|)
expr_stmt|;
name|binary
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|disposeThreadExecutionContext
parameter_list|(
name|TestContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|context
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"bundlingEnabled: %s, oakResourceEnabled: %s, readerEnabled: %s, bundlingMode: %s%n"
argument_list|,
name|bundlingEnabled
argument_list|,
name|oakResourceEnabled
argument_list|,
name|readerEnabled
argument_list|,
name|bundlingMode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|statsNames
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"Reader"
block|,
literal|"Mutator"
block|,
literal|"Assets#"
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|statsFormats
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"%8d"
block|,
literal|"%6d"
block|,
literal|"%6d"
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
index|[]
name|statsValues
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
name|reader
operator|.
name|readCount
block|,
name|mutator
operator|.
name|mutationCount
block|,
name|assetCount
operator|.
name|get
argument_list|()
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|comment
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|commentElements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|bundlingEnabled
condition|)
block|{
name|commentElements
operator|.
name|add
argument_list|(
literal|"bundling"
argument_list|)
expr_stmt|;
block|}
name|commentElements
operator|.
name|add
argument_list|(
name|contentNodeType
argument_list|)
expr_stmt|;
name|commentElements
operator|.
name|add
argument_list|(
name|bundlingMode
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|commentElements
argument_list|)
return|;
block|}
specifier|protected
class|class
name|TestContext
block|{
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
specifier|final
name|Queue
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|assetSampleSize
init|=
literal|50
decl_stmt|;
specifier|final
name|EvictingQueue
argument_list|<
name|String
argument_list|>
name|buffer
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|assetSampleSize
argument_list|)
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|assets
decl_stmt|;
specifier|private
name|int
name|assetCount
init|=
literal|0
decl_stmt|;
specifier|final
name|Node
name|dump
decl_stmt|;
specifier|public
name|TestContext
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dump
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|dump
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dump
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|public
name|String
name|pickRandomPath
parameter_list|()
block|{
if|if
condition|(
name|assets
operator|!=
literal|null
condition|)
block|{
return|return
name|assets
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|assets
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|buffer
operator|.
name|peek
argument_list|()
return|;
block|}
specifier|public
name|void
name|addAssetPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|buffer
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|assetCount
operator|%
name|assetSampleSize
operator|==
literal|0
condition|)
block|{
name|assets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addFolderPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TestContext
name|randomContext
parameter_list|()
block|{
return|return
name|contexts
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|contexts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|registerAssetNodeType
parameter_list|()
throws|throws
name|ParseException
throws|,
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|CndImporter
operator|.
name|registerNodeTypes
argument_list|(
operator|new
name|StringReader
argument_list|(
name|ASSET_NODE_TYPE_DEFN
argument_list|)
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
class|class
name|BundlingConfigInitializer
implements|implements
name|RepositoryInitializer
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|hasChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
condition|)
block|{
name|NodeBuilder
name|system
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|system
operator|.
name|hasChildNode
argument_list|(
name|DOCUMENT_NODE_STORE
argument_list|)
condition|)
block|{
name|NodeBuilder
name|dns
init|=
name|jcrChild
argument_list|(
name|system
argument_list|,
name|DOCUMENT_NODE_STORE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|bundlor
init|=
name|jcrChild
argument_list|(
name|dns
argument_list|,
name|BUNDLOR
argument_list|)
decl_stmt|;
name|addPattern
argument_list|(
name|bundlor
argument_list|,
literal|"nt:file"
argument_list|,
literal|"jcr:content"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|bundlingMode
condition|)
block|{
case|case
name|ALL
case|:
name|addPattern
argument_list|(
name|bundlor
argument_list|,
literal|"oak:Asset"
argument_list|,
literal|"jcr:content"
argument_list|,
literal|"jcr:content/metadata"
argument_list|,
literal|"jcr:content/renditions"
argument_list|,
literal|"jcr:content/renditions/**"
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXCLUDE_RENDITIONS
case|:
name|addPattern
argument_list|(
name|bundlor
argument_list|,
literal|"oak:Asset"
argument_list|,
literal|"jcr:content"
argument_list|,
literal|"jcr:content/metadata"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|addPattern
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|type
parameter_list|,
name|String
modifier|...
name|patterns
parameter_list|)
block|{
name|NodeBuilder
name|child
init|=
name|jcrChild
argument_list|(
name|builder
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"pattern"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|patterns
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeBuilder
name|jcrChild
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
block|}
specifier|private
enum|enum
name|FixNodeTypeIndexInitializer
implements|implements
name|RepositoryInitializer
block|{
name|INSTANCE
block|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|NodeBuilder
name|nodetype
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nodetype"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodetype
operator|.
name|exists
argument_list|()
condition|)
block|{
name|nodetype
operator|.
name|setProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"rep:Authorizable"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|Reader
implements|implements
name|Runnable
block|{
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|int
name|readCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|run0
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|run0
parameter_list|()
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
literal|25
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|randomContext
argument_list|()
operator|.
name|pickRandomPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|asset
init|=
name|session
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|asset
operator|.
name|getProperty
argument_list|(
literal|"jcr:content/metadata/status"
argument_list|)
expr_stmt|;
name|readCount
operator|++
expr_stmt|;
block|}
block|}
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|Mutator
implements|implements
name|Runnable
block|{
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|int
name|mutationCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|run0
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|run0
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|TestContext
name|ctx
init|=
name|randomContext
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|ctx
operator|.
name|pickRandomPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|asset
init|=
name|session
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Node
name|metadata
init|=
name|asset
operator|.
name|getNode
argument_list|(
literal|"jcr:content/metadata"
argument_list|)
decl_stmt|;
name|String
name|status
init|=
name|metadata
operator|.
name|getProperty
argument_list|(
literal|"status"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"status"
argument_list|,
name|Status
operator|.
name|valueOf
argument_list|(
name|status
argument_list|)
operator|.
name|next
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|mutationCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

