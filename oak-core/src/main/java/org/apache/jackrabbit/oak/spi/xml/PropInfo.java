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
name|spi
operator|.
name|xml
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|PropertyDefinition
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
name|Lists
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
name|nodetype
operator|.
name|EffectiveNodeType
import|;
end_import

begin_comment
comment|/**  * Information about a property being imported. This class is used  * by the XML import handlers to pass the parsed property information  * to the import process.  *<p>  * In addition to carrying the actual property data, instances of this  * class also know how to apply that data when imported either to a  * {@link javax.jcr.Node} instance through a session or directly to a  * {@link org.apache.jackrabbit.oak.api.Tree} instance on the oak level.  */
end_comment

begin_class
specifier|public
class|class
name|PropInfo
block|{
comment|/**      * String of the property being imported.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Type of the property being imported.      */
specifier|private
specifier|final
name|int
name|type
decl_stmt|;
comment|/**      * Value(s) of the property being imported.      */
specifier|private
specifier|final
name|TextValue
index|[]
name|values
decl_stmt|;
comment|/**      * Hint indicating whether the property is multi- or single-value      */
specifier|public
enum|enum
name|MultipleStatus
block|{
name|UNKNOWN
block|,
name|MULTIPLE
block|}
specifier|private
name|MultipleStatus
name|multipleStatus
decl_stmt|;
comment|/**      * Creates a property information instance.      *      * @param name name of the property being imported      * @param type type of the property being imported      * @param values value(s) of the property being imported      */
specifier|public
name|PropInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|TextValue
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|multipleStatus
operator|=
operator|(
name|values
operator|.
name|length
operator|==
literal|1
operator|)
condition|?
name|MultipleStatus
operator|.
name|UNKNOWN
else|:
name|MultipleStatus
operator|.
name|MULTIPLE
expr_stmt|;
block|}
comment|/**      * Creates a property information instance.      *      * @param name name of the property being imported      * @param type type of the property being imported      * @param values value(s) of the property being imported      * @param multipleStatus Hint indicating whether the property is      */
specifier|public
name|PropInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|TextValue
index|[]
name|values
parameter_list|,
name|MultipleStatus
name|multipleStatus
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|multipleStatus
operator|=
name|multipleStatus
expr_stmt|;
block|}
comment|/**      * Disposes all values contained in this property.      */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
for|for
control|(
name|TextValue
name|value
range|:
name|values
control|)
block|{
name|value
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getTargetType
parameter_list|(
name|PropertyDefinition
name|def
parameter_list|)
block|{
name|int
name|target
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
name|type
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
name|type
return|;
block|}
else|else
block|{
return|return
name|PropertyType
operator|.
name|STRING
return|;
block|}
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|TextValue
name|getTextValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|multipleStatus
operator|==
name|MultipleStatus
operator|.
name|MULTIPLE
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"TODO"
argument_list|)
throw|;
block|}
return|return
name|values
index|[
literal|0
index|]
return|;
block|}
specifier|public
name|TextValue
index|[]
name|getTextValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
specifier|public
name|Value
name|getValue
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|multipleStatus
operator|==
name|MultipleStatus
operator|.
name|MULTIPLE
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"TODO"
argument_list|)
throw|;
block|}
return|return
name|values
index|[
literal|0
index|]
operator|.
name|getValue
argument_list|(
name|targetType
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Value
argument_list|>
name|getValues
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|TextValue
name|value
range|:
name|values
control|)
block|{
name|vs
operator|.
name|add
argument_list|(
name|value
operator|.
name|getValue
argument_list|(
name|targetType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|vs
return|;
block|}
block|}
comment|//TODO check multivalue handling
specifier|public
name|PropertyDefinition
name|getPropertyDef
parameter_list|(
name|EffectiveNodeType
name|ent
parameter_list|)
block|{
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|definitions
init|=
name|ent
operator|.
name|getNamedPropertyDefinitions
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|knownType
init|=
name|getType
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|definitions
control|)
block|{
name|int
name|requiredType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|requiredType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|knownType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|requiredType
operator|==
name|knownType
operator|)
operator|&&
operator|(
name|def
operator|.
name|isMultiple
argument_list|()
operator|||
name|multipleStatus
operator|==
name|MultipleStatus
operator|.
name|UNKNOWN
operator|)
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
name|definitions
operator|=
name|ent
operator|.
name|getResidualPropertyDefinitions
argument_list|()
expr_stmt|;
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|definitions
control|)
block|{
name|int
name|requiredType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|requiredType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|knownType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|requiredType
operator|==
name|knownType
operator|)
operator|&&
operator|!
name|def
operator|.
name|isMultiple
argument_list|()
operator|&&
name|multipleStatus
operator|==
name|MultipleStatus
operator|.
name|UNKNOWN
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|definitions
control|)
block|{
name|int
name|requiredType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|requiredType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|knownType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
operator|||
name|requiredType
operator|==
name|knownType
operator|)
operator|&&
name|def
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

