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
name|core
operator|.
name|TreeLocation
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
name|Context
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|TreeUtil
import|;
end_import

begin_comment
comment|/**  * PrivilegeContext... TODO  */
end_comment

begin_class
specifier|final
class|class
name|PrivilegeContext
implements|implements
name|Context
implements|,
name|PrivilegeConstants
block|{
specifier|private
specifier|static
specifier|final
name|Context
name|INSTANCE
init|=
operator|new
name|PrivilegeContext
argument_list|()
decl_stmt|;
specifier|private
name|PrivilegeContext
parameter_list|()
block|{     }
specifier|static
name|Context
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|//------------------------------------------------------------< Context>---
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|definesTree
argument_list|(
name|parent
argument_list|)
operator|&&
name|PRIVILEGE_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|NT_REP_PRIVILEGE
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
name|TreeLocation
name|location
parameter_list|)
block|{
return|return
name|location
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildItems
parameter_list|(
name|Tree
name|parent
parameter_list|)
block|{
return|return
name|parent
operator|.
name|hasChild
argument_list|(
name|REP_PRIVILEGES
argument_list|)
operator|||
name|NT_REP_PRIVILEGES
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|parent
argument_list|)
argument_list|)
operator|||
name|NT_REP_PRIVILEGE
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|parent
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

