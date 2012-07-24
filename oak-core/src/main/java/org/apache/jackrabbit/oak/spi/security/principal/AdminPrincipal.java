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
name|principal
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

begin_comment
comment|/**  * Principal used to mark an administrator. The aim of this principal  * is to simplify the check whether a given set of principals is supplied with  * special (admin) access permissions. It may be used as the single or as  * additional non-group principal.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AdminPrincipal
implements|implements
name|Principal
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"administrator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|AdminPrincipal
name|INSTANCE
init|=
operator|new
name|AdminPrincipal
argument_list|()
decl_stmt|;
specifier|private
name|AdminPrincipal
parameter_list|()
block|{ }
comment|//----------------------------------------------------------< Principal>---
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|NAME
operator|+
literal|" principal"
return|;
block|}
block|}
end_class

end_unit

