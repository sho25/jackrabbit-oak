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
name|privilege
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|Function
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
name|Iterables
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
name|authorization
operator|.
name|PrivilegeManager
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
name|namepath
operator|.
name|NamePathMapper
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
name|privilege
operator|.
name|PrivilegeBits
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
name|PrivilegeBitsProvider
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
name|PrivilegeConfiguration
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

begin_class
specifier|public
class|class
name|JcrAllTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PrivilegeConstants
block|{
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
annotation|@
name|Override
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
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCalculatePermissionsAll
parameter_list|()
block|{
name|PrivilegeBits
name|all
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ALL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|Permissions
operator|.
name|ALL
operator|==
name|PrivilegeBits
operator|.
name|calculatePermissions
argument_list|(
name|all
argument_list|,
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Permissions
operator|.
name|ALL
operator|==
name|PrivilegeBits
operator|.
name|calculatePermissions
argument_list|(
name|all
argument_list|,
name|all
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAll
parameter_list|()
block|{
name|PrivilegeBits
name|all
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ALL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|all
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|JCR_ALL
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|all
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllAggregation
parameter_list|()
throws|throws
name|Exception
block|{
name|PrivilegeBits
name|all
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ALL
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|pMgr
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrivilegeManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|Privilege
argument_list|>
name|declaredAggr
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|pMgr
operator|.
name|getPrivilege
argument_list|(
name|JCR_ALL
argument_list|)
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|allAggregates
init|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|declaredAggr
argument_list|,
operator|new
name|Function
argument_list|<
name|Privilege
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Privilege
name|privilege
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|privilege
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|all2
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|allAggregates
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|all
argument_list|,
name|all2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|JCR_ALL
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|all2
argument_list|)
argument_list|)
expr_stmt|;
name|PrivilegeBits
name|bits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|allAggregates
control|)
block|{
name|bits
operator|.
name|add
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|all
argument_list|,
name|bits
operator|.
name|unmodifiable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
