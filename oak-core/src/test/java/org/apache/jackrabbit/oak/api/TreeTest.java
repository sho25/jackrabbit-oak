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
name|api
package|;
end_package

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
name|plugins
operator|.
name|commit
operator|.
name|AnnotatingConflictHandler
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
name|commit
operator|.
name|ConflictValidator
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|OakAssert
operator|.
name|assertSequence
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

begin_comment
comment|/**  * Contains tests related to {@link Tree}  */
end_comment

begin_class
specifier|public
class|class
name|TreeTest
block|{
specifier|private
name|ContentRepository
name|repository
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|repository
operator|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|ConflictValidator
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|AnnotatingConflictHandler
argument_list|()
block|{
comment|/**                  * Allow deleting changed node.                  * See {@link TreeTest#removeWithConcurrentOrderBefore()}                  */
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|Resolution
operator|.
name|OURS
return|;
block|}
block|}
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|repository
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r
init|=
name|s
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t
init|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// check again after commit
name|t
operator|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
comment|// :childOrder property invisible?
name|assertTrue
argument_list|(
name|t
operator|.
name|getProperty
argument_list|(
literal|":childOrder"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"must not have any properties"
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// other session wins
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
comment|// try again on current root
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node3"
argument_list|,
literal|"node1"
argument_list|,
literal|"node2"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeWithAdd
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// other session wins
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
comment|// try again on current root
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|,
literal|"node1"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeWithRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node4"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// other session wins
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
comment|// try again on current root
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeWithRemoveOtherSession
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node4"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node4"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// other session wins wrt ordering, but node4 is gone
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
comment|// try reorder again on current root
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeAllRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// now 'c' does not have ordered children anymore
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentOrderBeforeTargetRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|getChild
argument_list|(
literal|"node4"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t2
operator|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t2
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentAddChildOrderable
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// node3 from s1
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// node4 from s2
name|t2
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|,
literal|"node4"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentAddChild
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// node4 from s1
name|t1
operator|.
name|addChild
argument_list|(
literal|"node4"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// node5 from s2
name|t2
operator|.
name|addChild
argument_list|(
literal|"node5"
argument_list|)
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|r1
operator|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|t1
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"node1"
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|,
literal|"node4"
argument_list|,
literal|"node5"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeWithConcurrentOrderBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|s1
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r1
init|=
name|s1
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t1
init|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|s2
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|r2
init|=
name|s2
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t2
init|=
name|r2
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
decl_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|t1
operator|.
name|getChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|r1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t1
operator|=
name|r1
operator|.
name|getTree
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|t1
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
name|t2
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|r2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

