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
name|jcr
package|;
end_package

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
name|checkNotNull
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
name|atomic
operator|.
name|AtomicCounterEditor
operator|.
name|PROP_COUNTER
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
name|atomic
operator|.
name|AtomicCounterEditor
operator|.
name|PROP_INCREMENT
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_ATOMIC_COUNTER
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|AtomicCounterTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|AtomicCounterTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|incrementRootNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"normal node"
argument_list|)
decl_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"for normal nodes we expect the increment property to be treated as normal"
argument_list|,
name|node
operator|.
name|hasProperty
argument_list|(
name|PROP_INCREMENT
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|=
name|root
operator|.
name|addNode
argument_list|(
literal|"counterNode"
argument_list|)
expr_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|MIX_ATOMIC_COUNTER
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|node
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|node
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// increment again the same node
name|node
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|node
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// decrease the counter by 2
name|node
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
operator|-
literal|2L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|node
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// increase by 5
name|node
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|5L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|node
argument_list|,
literal|5
argument_list|)
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
block|}
specifier|private
specifier|static
name|void
name|assertCounter
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Node
name|counter
parameter_list|,
specifier|final
name|long
name|expectedCount
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|counter
operator|.
name|hasProperty
argument_list|(
name|PROP_COUNTER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedCount
argument_list|,
name|counter
operator|.
name|getProperty
argument_list|(
name|PROP_COUNTER
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|counter
operator|.
name|hasProperty
argument_list|(
name|PROP_INCREMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|incrementNonRootNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|counter
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"counter"
argument_list|)
decl_stmt|;
name|counter
operator|.
name|addMixin
argument_list|(
name|MIX_ATOMIC_COUNTER
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|counter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|counter
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|counter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// increment again the same node
name|counter
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|counter
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// decrease the counter by 2
name|counter
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
operator|-
literal|2L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|counter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// increase by 5
name|counter
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
literal|5L
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
name|counter
argument_list|,
literal|5
argument_list|)
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
block|}
block|}
end_class

end_unit

