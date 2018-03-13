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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|RepositoryContext
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
name|core
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
name|core
operator|.
name|config
operator|.
name|RepositoryConfig
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
name|DocumentNodeStoreBuilder
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
name|SegmentNodeStore
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
name|SegmentNodeStoreBuilders
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
name|memory
operator|.
name|MemoryStore
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
name|junit
operator|.
name|Assert
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|Node
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
name|Collection
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
name|Random
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
name|fail
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|LongNameTest
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
name|LongNameTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Short parent, short name"
block|,
literal|349
block|,
literal|150
block|,
literal|false
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Short parent, long name"
block|,
literal|349
block|,
literal|151
block|,
literal|false
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Long parent, short name"
block|,
literal|350
block|,
literal|150
block|,
literal|false
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Long parent, long name"
block|,
literal|350
block|,
literal|151
block|,
literal|true
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|shouldBeSkipped
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|crxRepo
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|LongNameTest
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|parentPathLength
parameter_list|,
name|int
name|nodeNameLength
parameter_list|,
name|boolean
name|shouldBeSkipped
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|generatePath
argument_list|(
name|parentPathLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|generateNodeName
argument_list|(
name|nodeNameLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|shouldBeSkipped
operator|=
name|shouldBeSkipped
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMigrationToDocStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|RepositoryException
block|{
name|SegmentNodeStore
name|src
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|dst
init|=
name|DocumentNodeStoreBuilder
operator|.
name|newDocumentNodeStoreBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|RepositorySidegrade
name|sidegrade
init|=
operator|new
name|RepositorySidegrade
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
decl_stmt|;
name|sidegrade
operator|.
name|setFilterLongNames
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sidegrade
operator|.
name|copy
argument_list|()
expr_stmt|;
name|NodeState
name|parent
init|=
name|getParent
argument_list|(
name|dst
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Parent should exists"
argument_list|,
name|parent
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldBeSkipped
condition|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Node shouldn't exists"
argument_list|,
name|parent
operator|.
name|hasChildNode
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Node should exists"
argument_list|,
name|parent
operator|.
name|hasChildNode
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeOnDocStore
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|DocumentNodeStore
name|docStore
init|=
name|DocumentNodeStoreBuilder
operator|.
name|newDocumentNodeStoreBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|createNodes
argument_list|(
name|docStore
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldBeSkipped
condition|)
block|{
name|fail
argument_list|(
literal|"It shouldn't be possible to create a node"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldBeSkipped
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"It should be possible to create a node"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testUpgradeToDocStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|RepositoryException
block|{
name|File
name|root
init|=
name|crxRepo
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
literal|"source"
argument_list|)
decl_stmt|;
name|source
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|RepositoryImpl
name|sourceRepository
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|source
argument_list|)
argument_list|)
operator|.
name|getRepository
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|sourceRepository
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
decl_stmt|;
try|try
block|{
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|addNode
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|sourceRepository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DocumentNodeStore
name|dst
init|=
name|DocumentNodeStoreBuilder
operator|.
name|newDocumentNodeStoreBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|RepositoryContext
name|ctx
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|RepositoryUpgrade
name|upgrade
init|=
operator|new
name|RepositoryUpgrade
argument_list|(
name|ctx
argument_list|,
name|dst
argument_list|)
decl_stmt|;
name|upgrade
operator|.
name|setCheckLongNames
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|upgrade
operator|.
name|copy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldBeSkipped
condition|)
block|{
name|fail
argument_list|(
literal|"Jackrabbit2 Lucene index should be used to inform about the node with long name"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldBeSkipped
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Upgrade should be successful"
argument_list|)
expr_stmt|;
block|}
block|}
name|ctx
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|ctx
operator|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|upgrade
operator|=
operator|new
name|RepositoryUpgrade
argument_list|(
name|ctx
argument_list|,
name|dst
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|setCheckLongNames
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|copy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|parent
init|=
name|getParent
argument_list|(
name|dst
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Parent should exists"
argument_list|,
name|parent
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldBeSkipped
condition|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Node shouldn't exists"
argument_list|,
name|parent
operator|.
name|hasChildNode
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Node should exists"
argument_list|,
name|parent
operator|.
name|hasChildNode
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createNodes
parameter_list|(
name|NodeStore
name|ns
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|root
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|nb
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|root
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
specifier|private
specifier|static
name|String
name|generatePath
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|1
condition|)
block|{
return|return
literal|"/"
return|;
block|}
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|StringBuilder
name|path
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|path
operator|.
name|length
argument_list|()
operator|<
name|length
condition|)
block|{
name|int
name|remaining
init|=
name|length
operator|-
name|path
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|==
literal|1
condition|)
block|{
name|path
operator|.
name|append
argument_list|(
name|generateNodeName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|remaining
operator|--
expr_stmt|;
name|path
operator|.
name|append
argument_list|(
name|generateNodeName
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
literal|150
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|generateNodeName
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|StringBuilder
name|nodeName
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodeName
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|'z'
operator|-
literal|'a'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeName
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|NodeState
name|getParent
parameter_list|(
name|NodeStore
name|ns
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
block|}
end_class

end_unit

