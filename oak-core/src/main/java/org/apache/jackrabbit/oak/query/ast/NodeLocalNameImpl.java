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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|PropertyType
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
name|PropertyValue
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
name|Type
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
name|commons
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
name|index
operator|.
name|FilterImpl
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
name|memory
operator|.
name|PropertyValues
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
name|query
operator|.
name|QueryConstants
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
name|query
operator|.
name|QueryIndex
operator|.
name|OrderEntry
import|;
end_import

begin_comment
comment|/**  * The function "localname(..)".  */
end_comment

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
literal|"localname("
operator|+
name|quote
argument_list|(
name|selectorName
argument_list|)
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
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyExistenceImpl
name|getPropertyExistence
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|selector
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyValue
name|currentProperty
parameter_list|()
block|{
name|String
name|path
init|=
name|selector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|getLocalName
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// TODO reverse namespace remapping?
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|localName
argument_list|)
return|;
block|}
specifier|static
name|String
name|getLocalName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
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
return|return
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|PropertyValue
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|operator
operator|==
name|Operator
operator|.
name|NOT_EQUAL
operator|&&
name|v
operator|!=
literal|null
condition|)
block|{
comment|// not supported
return|return;
block|}
name|String
name|name
init|=
name|NodeNameImpl
operator|.
name|getName
argument_list|(
name|query
argument_list|,
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|f
operator|.
name|getSelector
argument_list|()
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
operator|&&
name|NodeNameImpl
operator|.
name|supportedOperator
argument_list|(
name|operator
argument_list|)
condition|)
block|{
name|f
operator|.
name|restrictProperty
argument_list|(
name|QueryConstants
operator|.
name|RESTRICTION_LOCAL_NAME
argument_list|,
name|operator
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictList
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|List
argument_list|<
name|PropertyValue
argument_list|>
name|list
parameter_list|)
block|{
comment|// optimizations of type "LOCALNAME(..) IN(A, B)" are not supported
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFunction
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|"@"
operator|+
name|QueryConstants
operator|.
name|RESTRICTION_LOCAL_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsRangeConditions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRestrictSelector
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
return|;
block|}
annotation|@
name|Override
name|int
name|getPropertyType
parameter_list|()
block|{
return|return
name|PropertyType
operator|.
name|STRING
return|;
block|}
annotation|@
name|Override
specifier|public
name|DynamicOperandImpl
name|createCopy
parameter_list|()
block|{
return|return
operator|new
name|NodeLocalNameImpl
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|OrderEntry
name|getOrderEntry
parameter_list|(
name|SelectorImpl
name|s
parameter_list|,
name|OrderingImpl
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
comment|// ordered by a different selector
return|return
literal|null
return|;
block|}
return|return
operator|new
name|OrderEntry
argument_list|(
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|getFunction
argument_list|(
name|s
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|o
operator|.
name|isDescending
argument_list|()
condition|?
name|OrderEntry
operator|.
name|Order
operator|.
name|DESCENDING
else|:
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
argument_list|)
return|;
block|}
block|}
end_class

end_unit

