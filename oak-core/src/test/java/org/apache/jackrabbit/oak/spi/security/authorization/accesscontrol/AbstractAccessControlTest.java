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
name|accesscontrol
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
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
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
name|security
operator|.
name|AccessControlException
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
name|Root
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
name|name
operator|.
name|ReadWriteNamespaceRegistry
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
name|AuthorizationConfiguration
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
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionProvider
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAccessControlTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|protected
name|void
name|registerNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|NamespaceRegistry
name|nsRegistry
init|=
operator|new
name|ReadWriteNamespaceRegistry
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Tree
name|getReadTree
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|nsRegistry
operator|.
name|registerNamespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
if|if
condition|(
name|restrictionProvider
operator|==
literal|null
condition|)
block|{
name|restrictionProvider
operator|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
expr_stmt|;
block|}
return|return
name|restrictionProvider
return|;
block|}
specifier|protected
name|PrivilegeBitsProvider
name|getBitsProvider
parameter_list|()
block|{
if|if
condition|(
name|bitsProvider
operator|==
literal|null
condition|)
block|{
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|bitsProvider
return|;
block|}
specifier|protected
name|Principal
name|getTestPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
specifier|protected
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|TestACE
argument_list|(
name|principal
argument_list|,
name|getBitsProvider
argument_list|()
operator|.
name|getBits
argument_list|(
name|privilegeNames
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
specifier|protected
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PrivilegeBits
name|bits
init|=
name|getBitsProvider
argument_list|()
operator|.
name|getBits
argument_list|(
name|privileges
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestACE
argument_list|(
name|principal
argument_list|,
name|bits
argument_list|,
name|isAllow
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|PrivilegeBits
name|bits
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|AccessControlException
block|{
return|return
operator|new
name|TestACE
argument_list|(
name|principal
argument_list|,
name|bits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
specifier|private
specifier|final
class|class
name|TestACE
extends|extends
name|ACE
block|{
specifier|private
name|TestACE
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|PrivilegeBits
name|privilegeBits
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|super
argument_list|(
name|principal
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getPrivileges
parameter_list|()
block|{
name|Set
argument_list|<
name|Privilege
argument_list|>
name|privileges
init|=
operator|new
name|HashSet
argument_list|<
name|Privilege
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|getPrivilegeBits
argument_list|()
argument_list|)
control|)
block|{
try|try
block|{
name|privileges
operator|.
name|add
argument_list|(
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
operator|.
name|getPrivilege
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|privileges
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|privileges
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

