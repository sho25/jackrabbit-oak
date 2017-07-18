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
name|composite
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|oak
operator|.
name|InitialContent
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
name|api
operator|.
name|ContentSession
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
name|PropertyValue
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
name|QueryEngine
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
name|Result
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
name|ResultRow
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
name|Root
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
name|IOUtils
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
name|DocumentMK
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
name|DocumentNodeStore
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|identifier
operator|.
name|IdentifierManager
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|memory
operator|.
name|MemoryNodeStore
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
name|memory
operator|.
name|PropertyValues
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
name|mount
operator|.
name|Mounts
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
name|security
operator|.
name|OpenSecurityProvider
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
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
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
name|text
operator|.
name|ParseException
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
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|Lists
operator|.
name|newArrayList
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
name|newHashSet
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
name|QueryEngine
operator|.
name|NO_MAPPINGS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|AtomicCompositeMergeTest
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
name|AtomicCompositeMergeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|THREADS
init|=
literal|6
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_UUID
init|=
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
decl_stmt|;
specifier|private
name|Closer
name|closer
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|initNodeStore
parameter_list|()
block|{
name|closer
operator|=
name|Closer
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|closeAll
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testLocalMerges
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|ParseException
throws|,
name|CommitFailedException
block|{
name|Oak
name|oak
init|=
name|getOak
argument_list|(
name|getCompositeNodeStore
argument_list|(
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|testAtomicMerges
argument_list|(
name|clusterId
lambda|->
name|oak
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistributedMerge
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|ParseException
throws|,
name|CommitFailedException
block|{
name|MemoryDocumentStore
name|sharedDocStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|testAtomicMerges
argument_list|(
name|clusterId
lambda|->
block|{
name|DocumentNodeStore
name|docNodeStore
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|sharedDocStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
parameter_list|()
lambda|->
name|docNodeStore
operator|.
name|dispose
argument_list|()
argument_list|)
expr_stmt|;
name|NodeStore
name|compositeNodeStore
init|=
name|getCompositeNodeStore
argument_list|(
name|docNodeStore
argument_list|)
decl_stmt|;
return|return
name|getOak
argument_list|(
name|compositeNodeStore
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testAtomicMerges
parameter_list|(
name|Function
argument_list|<
name|Integer
argument_list|,
name|Oak
argument_list|>
name|oakSupplier
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|ParseException
throws|,
name|CommitFailedException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|failedMerges
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
name|newHashSet
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|ContentSession
name|generalSession
init|=
name|oakSupplier
operator|.
name|apply
argument_list|(
literal|100
argument_list|)
operator|.
name|createContentSession
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|generalSession
argument_list|)
expr_stmt|;
name|waitForReindexing
argument_list|(
name|generalSession
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// sleep for a sec, so the new repository have chance to initialize itself
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|THREADS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"child_"
operator|+
name|i
decl_stmt|;
name|Oak
name|oak
init|=
name|oakSupplier
operator|.
name|apply
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ContentSession
name|session
init|=
name|oak
operator|.
name|createContentSession
argument_list|()
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started thread {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|TEST_UUID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Merged successfully the node /{}: {}"
argument_list|,
name|name
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
operator|+
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected failure"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|failedMerges
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't commit"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|threads
operator|.
name|forEach
argument_list|(
name|Thread
operator|::
name|start
argument_list|)
expr_stmt|;
name|threads
operator|.
name|forEach
argument_list|(
name|AtomicCompositeMergeTest
operator|::
name|join
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|uuidPaths
init|=
name|waitForUuid
argument_list|(
name|generalSession
argument_list|,
name|TEST_UUID
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"There should be just one indexed value for the TEST_UUID, but following are given: "
operator|+
name|uuidPaths
operator|+
literal|". Failed merge list: "
operator|+
name|failedMerges
argument_list|,
literal|1
argument_list|,
name|uuidPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There should be "
operator|+
operator|(
name|THREADS
operator|-
literal|1
operator|)
operator|+
literal|" failed merges, but following are given: "
operator|+
name|failedMerges
argument_list|,
name|THREADS
operator|-
literal|1
argument_list|,
name|failedMerges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|waitForUuid
parameter_list|(
name|ContentSession
name|session
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|ParseException
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
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|queryUuid
argument_list|(
name|session
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|result
return|;
block|}
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|queryUuid
parameter_list|(
name|ContentSession
name|session
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|ParseException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|bindings
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"id"
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM [nt:base] WHERE [jcr:uuid] = $id"
operator|+
name|QueryEngine
operator|.
name|INTERNAL_SQL2_QUERY
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|bindings
argument_list|,
name|NO_MAPPINGS
argument_list|)
decl_stmt|;
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|r
lambda|->
name|r
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|waitForReindexing
parameter_list|(
name|ContentSession
name|session
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|ParseException
block|{
name|String
name|tmpUuid
init|=
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"tmp"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|tmpUuid
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|waitForUuid
argument_list|(
name|session
argument_list|,
name|tmpUuid
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/tmp"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|join
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
specifier|static
name|void
name|sleep
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
if|if
condition|(
name|millis
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|millis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|NodeStore
name|getCompositeNodeStore
parameter_list|(
name|NodeStore
name|globalNodeStore
parameter_list|)
block|{
return|return
operator|new
name|CompositeNodeStore
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|,
name|globalNodeStore
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Oak
name|getOak
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
return|return
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
