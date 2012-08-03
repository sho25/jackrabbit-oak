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
name|authorization
operator|.
name|Permissions
import|;
end_import

begin_comment
comment|/**  * TODO  */
end_comment

begin_class
class|class
name|CompiledPermissionImpl
implements|implements
name|CompiledPermissions
block|{
name|CompiledPermissionImpl
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{      }
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isProperty
parameter_list|)
block|{
comment|// TODO
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|permissions
parameter_list|)
block|{
comment|// TODO
return|return
operator|(
name|permissions
operator|==
name|Permissions
operator|.
name|READ
operator|)
return|;
block|}
block|}
end_class

end_unit

