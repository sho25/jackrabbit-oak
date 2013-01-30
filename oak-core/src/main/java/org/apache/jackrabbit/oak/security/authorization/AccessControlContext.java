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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_comment
comment|/**  * AccessControlContext... TODO  */
end_comment

begin_class
specifier|final
class|class
name|AccessControlContext
implements|implements
name|Context
implements|,
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|Context
name|INSTANCE
init|=
operator|new
name|AccessControlContext
argument_list|()
decl_stmt|;
specifier|private
name|AccessControlContext
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
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
name|node
operator|.
name|getPrimaryNodeTypeName
argument_list|()
decl_stmt|;
return|return
name|AC_NODETYPE_NAMES
operator|.
name|contains
argument_list|(
name|ntName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

