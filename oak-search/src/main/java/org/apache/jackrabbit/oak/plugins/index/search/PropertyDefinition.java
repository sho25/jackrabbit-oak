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
name|plugins
operator|.
name|index
operator|.
name|search
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|property
operator|.
name|ValuePattern
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
name|index
operator|.
name|search
operator|.
name|IndexDefinition
operator|.
name|IndexingRule
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
name|index
operator|.
name|search
operator|.
name|util
operator|.
name|FunctionIndexProcessor
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
name|index
operator|.
name|search
operator|.
name|util
operator|.
name|IndexHelper
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|toArray
import|;
end_import

begin_import
import|import static
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
operator|.
name|elements
import|;
end_import

begin_import
import|import static
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
operator|.
name|isAbsolute
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|FIELD_BOOST
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|PROP_IS_REGEX
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|PROP_WEIGHT
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|query
operator|.
name|FulltextIndexPlanner
operator|.
name|DEFAULT_PROPERTY_WEIGHT
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|util
operator|.
name|ConfigUtil
operator|.
name|getOptionalValue
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyDefinition
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertyDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EMPTY_ANCESTORS
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
comment|/**      * The default boost: 1.0f.      */
specifier|static
specifier|final
name|float
name|DEFAULT_BOOST
init|=
literal|1.0f
decl_stmt|;
comment|/**      * Property name. By default derived from the NodeState name which has the      * property definition. However in case property name is a pattern, relative      * property etc then it should be defined via 'name' property in NodeState.      * In such case NodeState name can be set to anything      */
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|int
name|propertyType
decl_stmt|;
comment|/**      * The boost value for a property.      */
specifier|public
specifier|final
name|float
name|boost
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|isRegexp
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|index
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|stored
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|nodeScopeIndex
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|propertyIndex
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|analyzed
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|ordered
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|nullCheckEnabled
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|notNullCheckEnabled
decl_stmt|;
specifier|final
name|int
name|includedPropertyTypes
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|relative
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|useInSuggest
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|useInSpellcheck
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|facet
decl_stmt|;
specifier|public
specifier|final
name|String
index|[]
name|ancestors
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|excludeFromAggregate
decl_stmt|;
specifier|public
specifier|final
name|int
name|weight
decl_stmt|;
comment|/**      * Property name excluding the relativePath. For regular expression based definition      * its set to null      */
annotation|@
name|Nullable
specifier|public
specifier|final
name|String
name|nonRelativeName
decl_stmt|;
comment|/**      * For function-based indexes: the function name, in Polish notation.      */
specifier|public
specifier|final
name|String
name|function
decl_stmt|;
comment|/**      * For function-based indexes: the function code, as tokens.      */
specifier|public
specifier|final
name|String
index|[]
name|functionCode
decl_stmt|;
specifier|public
specifier|final
name|ValuePattern
name|valuePattern
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|sync
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|unique
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|useInSimilarity
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|similarityRerank
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|similarityTags
decl_stmt|;
specifier|public
name|PropertyDefinition
parameter_list|(
name|IndexingRule
name|idxDefn
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|NodeState
name|defn
parameter_list|)
block|{
name|this
operator|.
name|isRegexp
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|PROP_IS_REGEX
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|getName
argument_list|(
name|defn
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|relative
operator|=
name|isRelativeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|FIELD_BOOST
argument_list|,
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|PROP_WEIGHT
argument_list|,
name|DEFAULT_PROPERTY_WEIGHT
argument_list|)
expr_stmt|;
comment|//By default if a property is defined it is indexed
name|this
operator|.
name|index
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|stored
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_EXCERPT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeScopeIndex
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_NODE_SCOPE_INDEX
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//If boost is specified then that field MUST be analyzed
if|if
condition|(
name|defn
operator|.
name|hasProperty
argument_list|(
name|FIELD_BOOST
argument_list|)
condition|)
block|{
name|this
operator|.
name|analyzed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|analyzed
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_ANALYZED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|ordered
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_ORDERED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|includedPropertyTypes
operator|=
name|IndexDefinition
operator|.
name|getSupportedTypes
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_INCLUDED_TYPE
argument_list|,
name|IndexDefinition
operator|.
name|TYPES_ALLOW_ALL
argument_list|)
expr_stmt|;
comment|//TODO Add test case for above cases
name|this
operator|.
name|propertyType
operator|=
name|getPropertyType
argument_list|(
name|idxDefn
argument_list|,
name|nodeName
argument_list|,
name|defn
argument_list|)
expr_stmt|;
name|this
operator|.
name|useInSuggest
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_SUGGEST
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|useInSpellcheck
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_SPELLCHECK
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|useInSimilarity
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_SIMILARITY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|similarityRerank
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_SIMILARITY_RERANK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|similarityTags
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_SIMILARITY_TAGS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullCheckEnabled
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_NULL_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|notNullCheckEnabled
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_NOT_NULL_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|excludeFromAggregate
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_EXCLUDE_FROM_AGGREGATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonRelativeName
operator|=
name|determineNonRelativeName
argument_list|()
expr_stmt|;
name|this
operator|.
name|ancestors
operator|=
name|computeAncestors
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|facet
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_FACETS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|FunctionIndexProcessor
operator|.
name|convertToPolishNotation
argument_list|(
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|functionCode
operator|=
name|FunctionIndexProcessor
operator|.
name|getFunctionCode
argument_list|(
name|this
operator|.
name|function
argument_list|)
expr_stmt|;
name|this
operator|.
name|valuePattern
operator|=
operator|new
name|ValuePattern
argument_list|(
name|defn
argument_list|)
expr_stmt|;
name|this
operator|.
name|unique
operator|=
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_UNIQUE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|sync
operator|=
name|unique
operator|||
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_SYNC
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//If some property is set to sync then propertyIndex mode is always enabled
name|this
operator|.
name|propertyIndex
operator|=
name|sync
operator|||
name|getOptionalValueIfIndexed
argument_list|(
name|defn
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|validate
argument_list|()
expr_stmt|;
block|}
comment|/**      * If 'analyzed' is enabled then property value would be used to evaluate the      * contains clause related to those properties. In such mode also some properties      * would be skipped from analysis      *      * @param propertyName name of the property to check. As property definition might      *                     be regEx based this is required to be passed explicitly      * @return true if the property value should be tokenized/analyzed      */
specifier|public
name|boolean
name|skipTokenization
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
comment|//For regEx case check against a whitelist
if|if
condition|(
name|isRegexp
operator|&&
name|IndexHelper
operator|.
name|skipTokenization
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
operator|!
name|analyzed
return|;
block|}
specifier|public
name|boolean
name|fulltextEnabled
parameter_list|()
block|{
return|return
name|index
operator|&&
operator|(
name|analyzed
operator|||
name|nodeScopeIndex
operator|)
return|;
block|}
specifier|public
name|boolean
name|propertyIndexEnabled
parameter_list|()
block|{
return|return
name|index
operator|&&
name|propertyIndex
return|;
block|}
specifier|public
name|boolean
name|isTypeDefined
parameter_list|()
block|{
return|return
name|propertyType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
return|;
block|}
comment|/**      * Returns the property type. If no explicit type is defined the default is assumed      * to be {@link PropertyType#STRING}      *      * @return propertyType as per javax.jcr.PropertyType      */
specifier|public
name|int
name|getType
parameter_list|()
block|{
comment|//If no explicit type is defined we assume it to be string
return|return
name|isTypeDefined
argument_list|()
condition|?
name|propertyType
else|:
name|PropertyType
operator|.
name|STRING
return|;
block|}
specifier|public
name|boolean
name|includePropertyType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
name|IndexDefinition
operator|.
name|includePropertyType
argument_list|(
name|includedPropertyTypes
argument_list|,
name|type
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
literal|"PropertyDefinition{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", propertyType="
operator|+
name|propertyType
operator|+
literal|", boost="
operator|+
name|boost
operator|+
literal|", isRegexp="
operator|+
name|isRegexp
operator|+
literal|", index="
operator|+
name|index
operator|+
literal|", stored="
operator|+
name|stored
operator|+
literal|", nodeScopeIndex="
operator|+
name|nodeScopeIndex
operator|+
literal|", propertyIndex="
operator|+
name|propertyIndex
operator|+
literal|", analyzed="
operator|+
name|analyzed
operator|+
literal|", ordered="
operator|+
name|ordered
operator|+
literal|", useInSuggest="
operator|+
name|useInSuggest
operator|+
literal|", useInSimilarity="
operator|+
name|useInSimilarity
operator|+
literal|", nullCheckEnabled="
operator|+
name|nullCheckEnabled
operator|+
literal|", notNullCheckEnabled="
operator|+
name|notNullCheckEnabled
operator|+
literal|", function="
operator|+
name|function
operator|+
literal|'}'
return|;
block|}
specifier|static
name|boolean
name|isRelativeProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
operator|!
name|isAbsolute
argument_list|(
name|propertyName
argument_list|)
operator|&&
operator|!
name|FulltextIndexConstants
operator|.
name|REGEX_ALL_PROPS
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
operator|&&
name|PathUtils
operator|.
name|getNextSlash
argument_list|(
name|propertyName
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
return|;
block|}
comment|//~---------------------------------------------< internal>
specifier|private
name|boolean
name|getOptionalValueIfIndexed
parameter_list|(
name|NodeState
name|definition
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|defaultVal
parameter_list|)
block|{
comment|//If property is not to be indexed then all other config would be
comment|//set to false ignoring whatever is defined in config for them
if|if
condition|(
operator|!
name|index
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|getOptionalValue
argument_list|(
name|definition
argument_list|,
name|propName
argument_list|,
name|defaultVal
argument_list|)
return|;
block|}
specifier|private
name|void
name|validate
parameter_list|()
block|{
if|if
condition|(
name|nullCheckEnabled
operator|&&
name|isRegexp
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s can be set to true for property definition using "
operator|+
literal|"regular expression"
argument_list|,
name|FulltextIndexConstants
operator|.
name|PROP_NULL_CHECK_ENABLED
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|determineNonRelativeName
parameter_list|()
block|{
if|if
condition|(
name|isRegexp
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|relative
condition|)
block|{
return|return
name|name
return|;
block|}
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
index|[]
name|computeAncestors
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|FulltextIndexConstants
operator|.
name|REGEX_ALL_PROPS
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|EMPTY_ANCESTORS
return|;
block|}
else|else
block|{
return|return
name|toArray
argument_list|(
name|copyOf
argument_list|(
name|elements
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getName
parameter_list|(
name|NodeState
name|definition
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NAME
argument_list|)
decl_stmt|;
return|return
name|ps
operator|==
literal|null
condition|?
name|defaultName
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getPropertyType
parameter_list|(
name|IndexingRule
name|idxDefn
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|defn
parameter_list|)
block|{
name|int
name|type
init|=
name|PropertyType
operator|.
name|UNDEFINED
decl_stmt|;
if|if
condition|(
name|defn
operator|.
name|hasProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_TYPE
argument_list|)
condition|)
block|{
name|String
name|typeName
init|=
name|defn
operator|.
name|getString
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_TYPE
argument_list|)
decl_stmt|;
try|try
block|{
name|type
operator|=
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid property type {} for property {} in Index {}"
argument_list|,
name|typeName
argument_list|,
name|name
argument_list|,
name|idxDefn
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

