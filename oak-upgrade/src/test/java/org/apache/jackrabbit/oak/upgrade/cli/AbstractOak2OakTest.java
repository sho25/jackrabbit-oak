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
name|upgrade
operator|.
name|cli
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|io
operator|.
name|InputStream
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
name|SimpleCredentials
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|reference
operator|.
name|ReferenceIndexProvider
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeStore
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
name|upgrade
operator|.
name|RepositorySidegrade
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|NodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|SegmentTarNodeStoreContainer
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
name|BeforeClass
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractOak2OakTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractOak2OakTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|SegmentTarNodeStoreContainer
name|testContent
decl_stmt|;
specifier|private
name|NodeStore
name|destination
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|private
name|RepositoryImpl
name|repository
decl_stmt|;
specifier|private
name|String
name|checkpointReference
decl_stmt|;
specifier|protected
specifier|abstract
name|NodeStoreContainer
name|getSourceContainer
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|NodeStoreContainer
name|getDestinationContainer
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|String
index|[]
name|getArgs
parameter_list|()
function_decl|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|unpackSegmentRepo
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tempDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"test-segment-store"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tempDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|Util
operator|.
name|unzip
argument_list|(
name|AbstractOak2OakTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/segmentstore.zip"
argument_list|)
argument_list|,
name|tempDir
argument_list|)
expr_stmt|;
block|}
name|testContent
operator|=
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|source
init|=
name|getSourceContainer
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|initContent
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getSourceContainer
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
name|getArgs
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"oak2oak {}"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|OakUpgrade
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|createSession
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createSession
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|destination
operator|=
name|getDestinationContainer
argument_list|()
operator|.
name|open
argument_list|()
expr_stmt|;
name|repository
operator|=
operator|(
name|RepositoryImpl
operator|)
operator|new
name|Jcr
argument_list|(
name|destination
argument_list|)
operator|.
name|with
argument_list|(
literal|"oak.sling"
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ReferenceIndexProvider
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|clean
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|getDestinationContainer
argument_list|()
argument_list|)
expr_stmt|;
name|getDestinationContainer
argument_list|()
operator|.
name|clean
argument_list|()
expr_stmt|;
name|getSourceContainer
argument_list|()
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initContent
parameter_list|(
name|NodeStore
name|target
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|NodeStore
name|initialContent
init|=
name|testContent
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|RepositorySidegrade
name|sidegrade
init|=
operator|new
name|RepositorySidegrade
argument_list|(
name|initialContent
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|sidegrade
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|testContent
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|NodeBuilder
name|builder
init|=
name|target
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"checkpoint-state"
argument_list|,
literal|"before"
argument_list|)
expr_stmt|;
name|target
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|checkpointReference
operator|=
name|target
operator|.
name|checkpoint
argument_list|(
literal|60000
argument_list|,
name|singletonMap
argument_list|(
literal|"key"
argument_list|,
literal|"123"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"checkpoint-state"
argument_list|,
literal|"after"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|target
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|validateMigration
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|verifyContent
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|verifyBlob
argument_list|(
name|session
argument_list|)
expr_stmt|;
if|if
condition|(
name|supportsCheckpointMigration
argument_list|()
condition|)
block|{
name|verifyCheckpoint
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|verifyEmptyAsync
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|verifyContent
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"rep:root"
argument_list|,
name|root
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|root
operator|.
name|getMixinNodeTypes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"rep:AccessControllable"
argument_list|,
name|root
operator|.
name|getMixinNodeTypes
argument_list|()
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sling:redirect"
argument_list|,
name|root
operator|.
name|getProperty
argument_list|(
literal|"sling:resourceType"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|allow
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/apps"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sling:Folder"
argument_list|,
name|allow
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"admin"
argument_list|,
name|allow
operator|.
name|getProperty
argument_list|(
literal|"jcr:createdBy"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|nodeType
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/jcr:system/jcr:nodeTypes/sling:OrderedFolder"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"rep:NodeType"
argument_list|,
name|nodeType
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jcr:mixinTypes"
argument_list|,
name|nodeType
operator|.
name|getProperty
argument_list|(
literal|"rep:protectedProperties"
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|nodeType
operator|.
name|getProperty
argument_list|(
literal|"jcr:isAbstract"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|verifyBlob
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|Property
name|p
init|=
name|session
operator|.
name|getProperty
argument_list|(
literal|"/sling-logo.png/jcr:content/jcr:data"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|getBinary
argument_list|()
operator|.
name|getStream
argument_list|()
decl_stmt|;
name|String
name|expectedMD5
init|=
literal|"35504d8c59455ab12a31f3d06f139a05"
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|expectedMD5
argument_list|,
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|verifyCheckpoint
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"after"
argument_list|,
name|destination
operator|.
name|getRoot
argument_list|()
operator|.
name|getString
argument_list|(
literal|"checkpoint-state"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
init|=
name|destination
operator|.
name|checkpointInfo
argument_list|(
name|checkpointReference
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|info
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|checkpoint
init|=
name|destination
operator|.
name|retrieve
argument_list|(
name|checkpointReference
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"before"
argument_list|,
name|checkpoint
operator|.
name|getString
argument_list|(
literal|"checkpoint-state"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123"
argument_list|,
name|destination
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|":async"
argument_list|)
operator|.
name|getString
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
operator|new
name|String
index|[]
block|{
literal|"var"
block|,
literal|"etc"
block|,
literal|"sling.css"
block|,
literal|"apps"
block|,
literal|"libs"
block|,
literal|"sightly"
block|}
control|)
block|{
name|assertSameRecord
argument_list|(
name|destination
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|checkpoint
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertSameRecord
parameter_list|(
name|NodeState
name|ns1
parameter_list|,
name|NodeState
name|ns2
parameter_list|)
block|{
name|String
name|recordId1
init|=
name|getRecordId
argument_list|(
name|ns1
argument_list|)
decl_stmt|;
name|String
name|recordId2
init|=
name|getRecordId
argument_list|(
name|ns2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|recordId1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|recordId1
argument_list|,
name|recordId2
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getRecordId
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentNodeState
operator|)
name|node
operator|)
operator|.
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|instanceof
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
condition|)
block|{
return|return
operator|(
operator|(
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
operator|)
name|node
operator|)
operator|.
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// OAK-2869
specifier|private
name|void
name|verifyEmptyAsync
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|destination
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|":async"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|supportsCheckpointMigration
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

