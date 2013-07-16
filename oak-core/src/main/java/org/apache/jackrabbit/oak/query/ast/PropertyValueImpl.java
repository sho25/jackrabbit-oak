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
name|ArrayList
import|;
end_import

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
name|Locale
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
name|QueryImpl
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
name|SQL2Parser
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
name|spi
operator|.
name|query
operator|.
name|PropertyValues
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_comment
comment|/**  * A property expression.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyValueImpl
extends|extends
name|DynamicOperandImpl
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|private
specifier|final
name|int
name|propertyType
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|PropertyValueImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|this
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyValueImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|propertyType
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|propertyType
operator|=
name|propertyType
operator|==
literal|null
condition|?
name|PropertyType
operator|.
name|UNDEFINED
else|:
name|SQL2Parser
operator|.
name|getPropertyTypeFromName
argument_list|(
name|propertyType
argument_list|)
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
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|propertyName
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
name|String
name|s
init|=
name|quote
argument_list|(
name|selectorName
argument_list|)
operator|+
literal|'.'
operator|+
name|quote
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
name|s
operator|=
literal|"property("
operator|+
name|s
operator|+
literal|", '"
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|propertyType
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"')"
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsRangeConditions
parameter_list|()
block|{
comment|// the jcr:path pseudo-property doesn't support LIKE conditions,
comment|// because the path doesn't might be escaped, and possibly contain
comment|// expressions that would result in incorrect results (/test[1] for example)
return|return
operator|!
name|propertyName
operator|.
name|equals
argument_list|(
name|QueryImpl
operator|.
name|JCR_PATH
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyExistenceImpl
name|getPropertyExistence
parameter_list|()
block|{
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|PropertyExistenceImpl
argument_list|(
name|selector
argument_list|,
name|selectorName
argument_list|,
name|propertyName
argument_list|)
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
name|boolean
name|asterisk
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyName
argument_list|)
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|asterisk
condition|)
block|{
name|PropertyValue
name|p
init|=
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
return|return
name|matchesPropertyType
argument_list|(
name|p
argument_list|)
condition|?
name|p
else|:
literal|null
return|;
block|}
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|selector
operator|.
name|currentPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|asterisk
condition|)
block|{
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tree
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyState
name|p
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|matchesPropertyType
argument_list|(
name|p
argument_list|)
condition|?
name|PropertyValues
operator|.
name|create
argument_list|(
name|p
argument_list|)
else|:
literal|null
return|;
block|}
comment|// asterisk - create a multi-value property
comment|// warning: the returned property state may have a mixed type
comment|// (not all values may have the same type)
comment|// TODO currently all property values are converted to strings -
comment|// this doesn't play well with the idea that the types may be different
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|matchesPropertyType
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|values
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// "*"
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|matchesPropertyType
parameter_list|(
name|PropertyValue
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|propertyType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|value
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|propertyType
return|;
block|}
specifier|private
name|boolean
name|matchesPropertyType
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|propertyType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|state
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|propertyType
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
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector
condition|)
block|{
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
name|f
operator|.
name|restrictProperty
argument_list|(
name|propertyName
argument_list|,
name|operator
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
name|f
operator|.
name|restrictPropertyType
argument_list|(
name|propertyName
argument_list|,
name|operator
argument_list|,
name|propertyType
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector
condition|)
block|{
name|f
operator|.
name|restrictPropertyAsList
argument_list|(
name|propertyName
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
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
operator|==
name|selector
return|;
block|}
annotation|@
name|Override
name|int
name|getPropertyType
parameter_list|()
block|{
return|return
name|propertyType
return|;
block|}
block|}
end_class

end_unit

