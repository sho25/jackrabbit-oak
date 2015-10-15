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
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|ItemBasedPrincipal
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
name|cug
operator|.
name|CugExclude
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
name|principal
operator|.
name|AdminPrincipal
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
name|principal
operator|.
name|PrincipalImpl
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
name|principal
operator|.
name|SystemPrincipal
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
name|principal
operator|.
name|SystemUserPrincipal
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

begin_class
specifier|public
class|class
name|CugExcludeDefaultTest
block|{
name|CugExclude
name|exclude
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|exclude
operator|=
name|createInstance
argument_list|()
expr_stmt|;
block|}
name|CugExclude
name|createInstance
parameter_list|()
block|{
return|return
operator|new
name|CugExclude
operator|.
name|Default
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyPrincipalSet
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemPrincipal
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdminPrincipal
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
operator|new
name|AdminPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"admin"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemUserPrincipal
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
operator|new
name|SystemUserPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrincipals
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|ItemBasedPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|"/path"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
name|assertFalse
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|p
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMixedPrincipals
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|SystemUserPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exclude
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

