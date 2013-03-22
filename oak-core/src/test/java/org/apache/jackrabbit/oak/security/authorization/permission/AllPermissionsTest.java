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
name|permission
package|;
end_package

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
name|List
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
name|AbstractSecurityTest
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
name|api
operator|.
name|Tree
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
name|authorization
operator|.
name|permission
operator|.
name|Permissions
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
name|authorization
operator|.
name|permission
operator|.
name|ReadStatus
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
name|assertSame
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
comment|/**  * Test {@code AllPermissions}.  */
end_comment

begin_class
specifier|public
class|class
name|AllPermissionsTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|CompiledPermissions
name|all
init|=
name|AllPermissions
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// TODO
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus
parameter_list|()
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|all
operator|.
name|getReadStatus
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|all
operator|.
name|getReadStatus
argument_list|(
name|child
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PropertyState
name|ps
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|all
operator|.
name|getReadStatus
argument_list|(
name|tree
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGranted
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|all
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|all
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|prop
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|all
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|all
operator|.
name|isGranted
argument_list|(
name|child
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSame
parameter_list|()
block|{
name|assertSame
argument_list|(
name|all
argument_list|,
name|AllPermissions
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

