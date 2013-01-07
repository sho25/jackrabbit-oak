begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|query
operator|.
name|ast
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
name|PropertyValue
import|;
end_import

begin_comment
comment|/**  * A bind variable.  */
end_comment

begin_class
specifier|public
class|class
name|BindVariableValueImpl
extends|extends
name|StaticOperandImpl
block|{
specifier|private
specifier|final
name|String
name|bindVariableName
decl_stmt|;
specifier|public
name|BindVariableValueImpl
parameter_list|(
name|String
name|bindVariableName
parameter_list|)
block|{
name|this
operator|.
name|bindVariableName
operator|=
name|bindVariableName
expr_stmt|;
block|}
specifier|public
name|String
name|getBindVariableName
parameter_list|()
block|{
return|return
name|bindVariableName
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'$'
operator|+
name|bindVariableName
return|;
block|}
annotation|@
name|Override
name|PropertyValue
name|currentValue
parameter_list|()
block|{
return|return
name|query
operator|.
name|getBindVariableValue
argument_list|(
name|bindVariableName
argument_list|)
return|;
block|}
annotation|@
name|Override
name|int
name|getPropertyType
parameter_list|()
block|{
name|PropertyValue
name|v
init|=
name|currentValue
argument_list|()
decl_stmt|;
return|return
name|v
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
return|;
block|}
block|}
end_class

end_unit

