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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|AccessControlContext
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
name|AllPermissions
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
name|CompiledPermissions
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
name|SystemPrincipal
import|;
end_import

begin_comment
comment|/**  * PermissionProviderImpl... TODO  */
end_comment

begin_class
class|class
name|AccessControlContextImpl
implements|implements
name|AccessControlContext
block|{
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
name|AccessControlContextImpl
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
block|}
comment|//-----------------------------------------------< AccessControlContext>---
annotation|@
name|Override
specifier|public
name|CompiledPermissions
name|getPermissions
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|subject
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
operator|||
name|isAdmin
argument_list|(
name|principals
argument_list|)
condition|)
block|{
return|return
name|AllPermissions
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
comment|// TODO: replace with permissions based on ac evaluation
return|return
operator|new
name|CompiledPermissionImpl
argument_list|(
name|principals
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|boolean
name|isAdmin
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|AdminPrincipal
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

