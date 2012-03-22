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
name|mk
operator|.
name|util
operator|.
name|PathUtils
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
name|query
operator|.
name|ScalarImpl
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
name|query
operator|.
name|index
operator|.
name|Filter
import|;
end_import

begin_class
specifier|public
class|class
name|NodeLocalNameImpl
extends|extends
name|DynamicOperandImpl
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|NodeLocalNameImpl
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
block|}
specifier|public
name|String
name|getSelectorName
parameter_list|()
block|{
return|return
name|selectorName
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
literal|"LOCALNAME("
operator|+
name|getSelectorName
argument_list|()
operator|+
literal|')'
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector
operator|=
name|source
operator|.
name|getSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown selector: "
operator|+
name|selectorName
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ScalarImpl
name|currentValue
parameter_list|()
block|{
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|selector
operator|.
name|currentPath
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
comment|// TODO LOCALNAME: evaluation of local name might not be correct
name|String
name|localName
init|=
name|colon
operator|<
literal|0
condition|?
name|name
else|:
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
name|query
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|localName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|Filter
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|ScalarImpl
name|v
parameter_list|)
block|{
comment|// TODO support LOCALNAME index conditions
block|}
block|}
end_class

end_unit

