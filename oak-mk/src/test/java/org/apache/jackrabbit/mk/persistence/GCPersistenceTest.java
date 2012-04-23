begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|persistence
package|;
end_package

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
name|assertTrue
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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|Id
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
name|mk
operator|.
name|model
operator|.
name|MutableCommit
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
name|mk
operator|.
name|model
operator|.
name|MutableNode
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
name|mk
operator|.
name|model
operator|.
name|StoredNode
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
name|mk
operator|.
name|store
operator|.
name|NotFoundException
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
name|mk
operator|.
name|util
operator|.
name|IOUtils
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
name|Test
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
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_comment
comment|/**  * Tests verifying the contract provided by {@link GCPersistence}.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|GCPersistenceTest
block|{
specifier|private
name|Class
argument_list|<
name|GCPersistence
argument_list|>
name|pmClass
decl_stmt|;
specifier|private
name|GCPersistence
name|pm
decl_stmt|;
specifier|public
name|GCPersistenceTest
parameter_list|(
name|Class
argument_list|<
name|GCPersistence
argument_list|>
name|pmClass
parameter_list|)
block|{
name|this
operator|.
name|pmClass
operator|=
name|pmClass
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|pm
operator|=
name|pmClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|pm
operator|.
name|initialize
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/mk"
argument_list|)
argument_list|)
expr_stmt|;
comment|// start with empty repository
name|pm
operator|.
name|start
argument_list|()
expr_stmt|;
name|pm
operator|.
name|sweep
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
name|classes
parameter_list|()
block|{
name|Class
index|[]
index|[]
name|pmClasses
init|=
operator|new
name|Class
index|[]
index|[]
block|{
block|{
name|H2Persistence
operator|.
name|class
block|}
block|,
block|{
name|InMemPersistence
operator|.
name|class
block|}
block|}
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|pmClasses
argument_list|)
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|pm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOldNodeIsSwept
parameter_list|()
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
operator|new
name|MutableNode
argument_list|(
literal|null
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|Id
name|id
init|=
name|pm
operator|.
name|writeNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pm
operator|.
name|start
argument_list|()
expr_stmt|;
name|pm
operator|.
name|sweep
argument_list|()
expr_stmt|;
try|try
block|{
name|pm
operator|.
name|readNode
argument_list|(
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
comment|/* expected */
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMarkedNodeIsNotSwept
parameter_list|()
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
operator|new
name|MutableNode
argument_list|(
literal|null
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|Id
name|id
init|=
name|pm
operator|.
name|writeNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|// small delay needed
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|pm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// old node must not be marked
name|assertTrue
argument_list|(
name|pm
operator|.
name|markNode
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|pm
operator|.
name|readNode
argument_list|(
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewNodeIsNotSwept
parameter_list|()
throws|throws
name|Exception
block|{
name|pm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MutableNode
name|node
init|=
operator|new
name|MutableNode
argument_list|(
literal|null
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|Id
name|id
init|=
name|pm
operator|.
name|writeNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|// new node must already be marked
name|assertFalse
argument_list|(
name|pm
operator|.
name|markNode
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|pm
operator|.
name|readNode
argument_list|(
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|MutableCommit
name|c1
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|c1
operator|.
name|setRootNodeId
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeCommit
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|1
argument_list|)
argument_list|,
name|c1
argument_list|)
expr_stmt|;
name|MutableCommit
name|c2
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|c2
operator|.
name|setParentId
argument_list|(
name|c1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c2
operator|.
name|setRootNodeId
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeCommit
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|2
argument_list|)
argument_list|,
name|c2
argument_list|)
expr_stmt|;
name|pm
operator|.
name|start
argument_list|()
expr_stmt|;
name|c2
operator|=
operator|new
name|MutableCommit
argument_list|()
expr_stmt|;
name|c2
operator|.
name|setRootNodeId
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|pm
operator|.
name|replaceCommit
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|2
argument_list|)
argument_list|,
name|c2
argument_list|)
expr_stmt|;
name|pm
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|pm
operator|.
name|readCommit
argument_list|(
name|Id
operator|.
name|fromLong
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

