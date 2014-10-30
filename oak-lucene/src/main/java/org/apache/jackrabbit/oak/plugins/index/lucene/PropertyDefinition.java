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
name|lucene
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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

begin_class
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
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
specifier|private
specifier|final
name|int
name|propertyType
decl_stmt|;
specifier|public
name|PropertyDefinition
parameter_list|(
name|IndexDefinition
name|idxDefn
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeBuilder
name|defn
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
name|definition
operator|=
name|defn
expr_stmt|;
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
name|LuceneIndexConstants
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
name|LuceneIndexConstants
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
name|this
operator|.
name|propertyType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
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

