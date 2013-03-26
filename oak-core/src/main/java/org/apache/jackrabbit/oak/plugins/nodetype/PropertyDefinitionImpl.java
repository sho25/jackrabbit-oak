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
name|plugins
operator|.
name|nodetype
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
import|;
end_import

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
name|QueryObjectModelConstants
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
name|JcrConstants
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
name|namepath
operator|.
name|NamePathMapper
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
name|value
operator|.
name|ValueFactoryImpl
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|BINARY
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|BOOLEAN
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|DATE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|DECIMAL
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|DOUBLE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|LONG
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|NAME
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|PATH
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|REFERENCE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|UNDEFINED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|URI
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|WEAKREFERENCE
import|;
end_import

begin_comment
comment|/**  *<pre>  * [nt:propertyDefinition]  *   ...  * - jcr:requiredType (STRING) protected mandatory  *< 'STRING', 'URI', 'BINARY', 'LONG', 'DOUBLE',  *     'DECIMAL', 'BOOLEAN', 'DATE', 'NAME', 'PATH',  *     'REFERENCE', 'WEAKREFERENCE', 'UNDEFINED'  * - jcr:valueConstraints (STRING) protected multiple  * - jcr:defaultValues (UNDEFINED) protected multiple  * - jcr:multiple (BOOLEAN) protected mandatory  * - jcr:availableQueryOperators (NAME) protected mandatory multiple  * - jcr:isFullTextSearchable (BOOLEAN) protected mandatory  * - jcr:isQueryOrderable (BOOLEAN) protected mandatory  *</pre>  */
end_comment

begin_class
class|class
name|PropertyDefinitionImpl
extends|extends
name|ItemDefinitionImpl
implements|implements
name|PropertyDefinition
block|{
specifier|private
specifier|static
specifier|final
name|Value
index|[]
name|NO_VALUES
init|=
operator|new
name|Value
index|[
literal|0
index|]
decl_stmt|;
name|PropertyDefinitionImpl
parameter_list|(
name|Tree
name|definition
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|NamePathMapper
name|mapper
parameter_list|)
block|{
name|super
argument_list|(
name|definition
argument_list|,
name|type
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the numeric constant value of the type with the specified name.      *      * In contrast to {@link javax.jcr.PropertyType#valueFromName(String)} this method      * requires all type names to be all upper case.      * See also: OAK-294 and http://java.net/jira/browse/JSR_283-811      *      * @param name the name of the property type.      * @return the numeric constant value.      * @throws IllegalStateException if {@code name} is not a valid property type name.      */
specifier|public
specifier|static
name|int
name|valueFromName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"STRING"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|STRING
return|;
block|}
elseif|else
if|if
condition|(
literal|"BINARY"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|BINARY
return|;
block|}
elseif|else
if|if
condition|(
literal|"BOOLEAN"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|BOOLEAN
return|;
block|}
elseif|else
if|if
condition|(
literal|"LONG"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|LONG
return|;
block|}
elseif|else
if|if
condition|(
literal|"DOUBLE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|DOUBLE
return|;
block|}
elseif|else
if|if
condition|(
literal|"DECIMAL"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|DECIMAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"DATE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|DATE
return|;
block|}
elseif|else
if|if
condition|(
literal|"NAME"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|NAME
return|;
block|}
elseif|else
if|if
condition|(
literal|"PATH"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|PATH
return|;
block|}
elseif|else
if|if
condition|(
literal|"REFERENCE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|REFERENCE
return|;
block|}
elseif|else
if|if
condition|(
literal|"WEAKREFERENCE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|WEAKREFERENCE
return|;
block|}
elseif|else
if|if
condition|(
literal|"URI"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|URI
return|;
block|}
elseif|else
if|if
condition|(
literal|"UNDEFINED"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|UNDEFINED
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unknown property type: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
comment|//-------------------------------------------------< PropertyDefinition>---
annotation|@
name|Override
specifier|public
name|int
name|getRequiredType
parameter_list|()
block|{
name|String
name|string
init|=
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_REQUIREDTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|string
operator|!=
literal|null
condition|)
block|{
return|return
name|valueFromName
argument_list|(
name|string
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|UNDEFINED
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getValueConstraints
parameter_list|()
block|{
comment|// TODO: namespace mapping?
return|return
name|getStrings
argument_list|(
name|JcrConstants
operator|.
name|JCR_VALUECONSTRAINTS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getDefaultValues
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DEFAULTVALUES
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|property
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
return|return
name|values
operator|.
name|toArray
argument_list|(
name|NO_VALUES
argument_list|)
return|;
block|}
else|else
block|{
name|Value
name|value
init|=
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|property
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|Value
index|[]
block|{
name|value
block|}
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_MULTIPLE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAvailableQueryOperators
parameter_list|()
block|{
name|String
index|[]
name|operators
init|=
name|getStrings
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_AVAILABLE_QUERY_OPERATORS
argument_list|)
decl_stmt|;
if|if
condition|(
name|operators
operator|==
literal|null
condition|)
block|{
name|operators
operator|=
operator|new
name|String
index|[]
block|{
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_EQUAL_TO
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_NOT_EQUAL_TO
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_GREATER_THAN
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_LESS_THAN
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO
block|,
name|QueryObjectModelConstants
operator|.
name|JCR_OPERATOR_LIKE
block|}
expr_stmt|;
block|}
return|return
name|operators
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFullTextSearchable
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_IS_FULLTEXT_SEARCHABLE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryOrderable
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_IS_QUERY_ORDERABLE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

