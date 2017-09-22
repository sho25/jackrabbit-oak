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
name|spi
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|privilege
operator|.
name|PrivilegeConstants
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
name|mockito
operator|.
name|Mockito
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

begin_class
specifier|public
class|class
name|EmptyPermissionProviderTest
block|{
specifier|private
specifier|final
name|PermissionProvider
name|emptyProvider
init|=
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivileges
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|emptyProvider
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivileges
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|emptyProvider
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRepositoryPermission
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RepositoryPermission
operator|.
name|EMPTY
argument_list|,
name|emptyProvider
operator|.
name|getRepositoryPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermission
parameter_list|()
block|{
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|emptyProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGranted
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|emptyProvider
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|emptyProvider
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedActions
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|emptyProvider
operator|.
name|isGranted
argument_list|(
literal|"/"
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

