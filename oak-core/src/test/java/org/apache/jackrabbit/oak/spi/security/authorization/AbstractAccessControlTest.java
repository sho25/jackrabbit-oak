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
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
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
name|restriction
operator|.
name|RestrictionProvider
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
name|getSecurityProvider
argument_list|()
operator|.
name|getAccessControlConfiguration
argument_list|()
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
block|}
end_class

end_unit

