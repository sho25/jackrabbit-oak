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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|ValueFactory
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
name|api
operator|.
name|TreeLocation
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
name|namepath
operator|.
name|NameMapper
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
name|memory
operator|.
name|PropertyStates
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
name|Conversions
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
name|util
operator|.
name|Text
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|api
operator|.
name|Type
operator|.
name|BOOLEAN
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
name|api
operator|.
name|Type
operator|.
name|DATE
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
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_comment
comment|/**  * Utility class for accessing and writing typed content of a tree.  */
end_comment

begin_class
specifier|public
class|class
name|NodeUtil
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
name|NodeUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|public
name|NodeUtil
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|NameMapper
name|mapper
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|checkNotNull
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|checkNotNull
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeUtil
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|this
argument_list|(
name|tree
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|tree
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeUtil
name|getParent
parameter_list|()
block|{
return|return
operator|new
name|NodeUtil
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|,
name|mapper
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|tree
operator|.
name|isRoot
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeUtil
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|(
name|child
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|NodeUtil
argument_list|(
name|child
argument_list|,
name|mapper
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeUtil
name|addChild
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|primaryNodeTypeName
parameter_list|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NodeUtil
name|childUtil
init|=
operator|new
name|NodeUtil
argument_list|(
name|child
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
name|childUtil
operator|.
name|setName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|primaryNodeTypeName
argument_list|)
expr_stmt|;
return|return
name|childUtil
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeUtil
name|getOrAddChild
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|primaryTypeName
parameter_list|)
block|{
name|NodeUtil
name|child
init|=
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|(
name|child
operator|!=
literal|null
operator|)
condition|?
name|child
else|:
name|addChild
argument_list|(
name|name
argument_list|,
name|primaryTypeName
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeUtil
name|getOrAddTree
parameter_list|(
name|String
name|relativePath
parameter_list|,
name|String
name|primaryTypeName
parameter_list|)
block|{
if|if
condition|(
name|relativePath
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|getOrAddChild
argument_list|(
name|relativePath
argument_list|,
name|primaryTypeName
argument_list|)
return|;
block|}
else|else
block|{
name|TreeLocation
name|location
init|=
name|LocationUtil
operator|.
name|getTreeLocation
argument_list|(
name|tree
operator|.
name|getLocation
argument_list|()
argument_list|,
name|relativePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|getTree
argument_list|()
operator|==
literal|null
condition|)
block|{
name|NodeUtil
name|target
init|=
name|this
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|Text
operator|.
name|explode
argument_list|(
name|relativePath
argument_list|,
literal|'/'
argument_list|)
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesCurrent
argument_list|(
name|segment
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|PathUtils
operator|.
name|denotesParent
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|target
operator|=
name|target
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|target
operator|.
name|hasChild
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|target
operator|=
name|target
operator|.
name|getChild
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
name|target
operator|.
name|addChild
argument_list|(
name|segment
argument_list|,
name|primaryTypeName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|target
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeUtil
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
specifier|public
name|boolean
name|hasPrimaryNodeTypeName
parameter_list|(
name|String
name|ntName
parameter_list|)
block|{
return|return
name|ntName
operator|.
name|equals
argument_list|(
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|tree
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
operator|!
name|property
operator|.
name|isArray
argument_list|()
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
operator|!
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultValue
return|;
block|}
block|}
specifier|public
name|void
name|setString
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
index|[]
name|getStrings
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
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
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|setStrings
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getName
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getName
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
operator|!
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultValue
return|;
block|}
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|oakName
init|=
name|getOakName
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|oakName
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
index|[]
name|getNames
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|defaultValues
parameter_list|)
block|{
name|String
index|[]
name|strings
init|=
name|getStrings
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|==
literal|null
condition|)
block|{
name|strings
operator|=
name|defaultValues
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|strings
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|getJcrName
argument_list|(
name|strings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
specifier|public
name|void
name|setNames
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|GregorianCalendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|cal
argument_list|)
operator|.
name|toDate
argument_list|()
argument_list|,
name|DATE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|defaultValue
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
operator|!
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultValue
return|;
block|}
block|}
specifier|public
name|void
name|setLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|NodeUtil
argument_list|>
name|getNodes
parameter_list|(
name|String
name|namePrefix
parameter_list|)
block|{
name|List
argument_list|<
name|NodeUtil
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|namePrefix
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|NodeUtil
argument_list|(
name|child
argument_list|,
name|mapper
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
specifier|public
name|void
name|setValues
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
block|{
try|try
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to convert a default value"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setValues
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|Value
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|,
name|ValueFactory
name|vf
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|int
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
try|try
block|{
name|values
operator|.
name|add
argument_list|(
name|vf
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to convert a default value"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
block|{
name|String
name|oakName
init|=
name|mapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name:"
operator|+
name|jcrName
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|oakName
return|;
block|}
block|}
end_class

end_unit

