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
name|jcr
operator|.
name|query
operator|.
name|qom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|BindVariableValue
import|;
end_import

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|BindVariableValueImpl
extends|extends
name|StaticOperandImpl
implements|implements
name|BindVariableValue
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
annotation|@
name|Override
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
specifier|public
name|void
name|bindVariables
parameter_list|(
name|QueryObjectModelImpl
name|qom
parameter_list|)
block|{
name|qom
operator|.
name|addBindVariable
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

