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
name|CoreValue
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
name|plugins
operator|.
name|memory
operator|.
name|MultiPropertyState
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
comment|// TODO quote property names?
name|String
name|s
init|=
name|getSelectorName
argument_list|()
operator|+
literal|'.'
operator|+
name|propertyName
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
name|PropertyState
name|currentProperty
parameter_list|()
block|{
name|boolean
name|relative
init|=
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|asterisk
init|=
name|propertyName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|relative
operator|&&
operator|!
name|asterisk
condition|)
block|{
name|PropertyState
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
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|relative
condition|)
block|{
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|propertyName
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|tree
operator|.
name|hasChild
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|p
else|:
literal|null
return|;
block|}
comment|// asterisk - create a multi-value property
comment|// warning: the returned property state may have a mixed type
comment|// (not all values may have the same type)
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|CoreValue
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
if|if
condition|(
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|values
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|MultiPropertyState
name|mv
init|=
operator|new
name|MultiPropertyState
argument_list|(
literal|"*"
argument_list|,
name|values
argument_list|)
decl_stmt|;
return|return
name|mv
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
if|if
condition|(
name|state
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
init|=
name|state
operator|.
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// TODO how to retrieve the property type of an empty multi-value property?
comment|// currently it matches all property types
return|return
literal|true
return|;
block|}
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|==
name|propertyType
return|;
block|}
return|return
name|state
operator|.
name|getValue
argument_list|()
operator|.
name|getType
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
name|apply
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|CoreValue
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
block|}
end_class

end_unit

