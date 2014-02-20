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
name|security
operator|.
name|authorization
operator|.
name|evaluation
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
name|assertNotNull
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
name|Tree
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

begin_comment
comment|/**  * Test to make sure hidden trees are never exposed.  */
end_comment

begin_class
specifier|public
class|class
name|HiddenTreeTest
extends|extends
name|AbstractOakCoreTest
block|{
specifier|private
name|String
name|hiddenParentPath
init|=
literal|"/oak:index/nodetype"
decl_stmt|;
specifier|private
name|String
name|hiddenName
init|=
literal|":index"
decl_stmt|;
specifier|private
name|Tree
name|parent
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|hiddenParentPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parent
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasHiddenTree
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|parent
operator|.
name|hasChild
argument_list|(
name|hiddenName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1441"
argument_list|)
comment|// FIXME OAK-1441
specifier|public
name|void
name|testGetHiddenTree
parameter_list|()
block|{
name|Tree
name|hidden
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|hiddenName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|hidden
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hidden
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hidden
operator|.
name|getChildrenCount
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderBeforeOnHiddenTree
parameter_list|()
block|{
try|try
block|{
name|Tree
name|hidden
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|hiddenName
argument_list|)
decl_stmt|;
name|hidden
operator|.
name|orderBefore
argument_list|(
literal|"someother"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalStateException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetOrderableChildNodesOnHiddenTree
parameter_list|()
block|{
try|try
block|{
name|Tree
name|hidden
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|hiddenName
argument_list|)
decl_stmt|;
name|hidden
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalStateException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|Tree
name|hidden
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|hiddenName
argument_list|)
decl_stmt|;
name|hidden
operator|.
name|setOrderableChildren
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalStateException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHiddenChildren
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|children
init|=
name|parent
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|children
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHiddenChildrenCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|parent
operator|.
name|getChildrenCount
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1424"
argument_list|)
comment|// FIXME : OAK-1424
annotation|@
name|Test
specifier|public
name|void
name|testCreateHiddenChild
parameter_list|()
block|{
try|try
block|{
name|Tree
name|hidden
init|=
name|parent
operator|.
name|addChild
argument_list|(
literal|":hiddenChild"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

end_unit

