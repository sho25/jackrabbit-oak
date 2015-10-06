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
name|plugins
operator|.
name|segment
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
name|base
operator|.
name|Suppliers
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
name|NodeStateDiff
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
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
specifier|public
class|class
name|CancelableDiffTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testPropertyAddedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|PropertyState
name|after
init|=
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyChangedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|PropertyState
name|before
init|=
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|PropertyState
name|after
init|=
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyDeletedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|PropertyState
name|before
init|=
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAddedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|NodeState
name|after
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|childNodeAdded
argument_list|(
literal|"name"
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|childNodeAdded
argument_list|(
literal|"name"
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|childNodeAdded
argument_list|(
literal|"name"
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeChangedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|NodeState
name|before
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|childNodeChanged
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|childNodeChanged
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|childNodeChanged
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeDeletedInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
name|NodeState
name|before
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStateDiff
name|wrapped
init|=
name|mock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|wrapped
argument_list|)
operator|.
name|childNodeDeleted
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|false
argument_list|)
operator|.
name|childNodeDeleted
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newCancelableDiff
argument_list|(
name|wrapped
argument_list|,
literal|true
argument_list|)
operator|.
name|childNodeDeleted
argument_list|(
literal|"name"
argument_list|,
name|before
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeStateDiff
name|newCancelableDiff
parameter_list|(
name|NodeStateDiff
name|wrapped
parameter_list|,
name|boolean
name|cancel
parameter_list|)
block|{
return|return
operator|new
name|CancelableDiff
argument_list|(
name|wrapped
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
name|cancel
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

