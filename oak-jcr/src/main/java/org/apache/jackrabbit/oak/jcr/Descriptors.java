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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Repository
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
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|IDENTIFIER_STABILITY
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|LEVEL_1_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_AUTOCREATED_DEFINITIONS_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_INHERITANCE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_INHERITANCE_SINGLE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_MULTIPLE_BINARY_PROPERTIES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_MULTIVALUED_PROPERTIES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_ORDERABLE_CHILD_NODES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_OVERRIDES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_PRIMARY_ITEM_NAME_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_PROPERTY_TYPES
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_RESIDUAL_DEFINITIONS_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_SAME_NAME_SIBLINGS_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_UPDATE_IN_USE_SUPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|NODE_TYPE_MANAGEMENT_VALUE_CONSTRAINTS_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_ACCESS_CONTROL_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_ACTIVITIES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_BASELINES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_JOURNALED_OBSERVATION_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_LIFECYCLE_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_LOCKING_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_NODE_TYPE_MANAGEMENT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_OBSERVATION_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_QUERY_SQL_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_RETENTION_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_SHAREABLE_NODES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_SIMPLE_VERSIONING_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_TRANSACTIONS_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_UNFILED_CONTENT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_UPDATE_MIXIN_NODE_TYPES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_UPDATE_PRIMARY_NODE_TYPE_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_VERSIONING_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_XML_EXPORT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_XML_IMPORT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_FULL_TEXT_SEARCH_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_JOINS
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_JOINS_NONE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_LANGUAGES
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_STORED_QUERIES_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_XPATH_DOC_ORDER
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|QUERY_XPATH_POS_INDEX
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|REP_NAME_DESC
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|REP_VENDOR_DESC
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|REP_VENDOR_URL_DESC
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|SPEC_NAME_DESC
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|SPEC_VERSION_DESC
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|WRITE_SUPPORTED
import|;
end_import

begin_class
specifier|public
class|class
name|Descriptors
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Descriptor
argument_list|>
name|descriptors
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|Descriptors
parameter_list|(
name|ValueFactory
name|valueFactory
parameter_list|)
block|{
name|descriptors
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Descriptor
argument_list|>
argument_list|()
expr_stmt|;
name|Value
name|trueValue
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Value
name|falseValue
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|IDENTIFIER_STABILITY
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|Repository
operator|.
name|IDENTIFIER_STABILITY_METHOD_DURATION
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|LEVEL_1_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|LEVEL_2_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_NODE_TYPE_MANAGEMENT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_AUTOCREATED_DEFINITIONS_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_INHERITANCE
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|NODE_TYPE_MANAGEMENT_INHERITANCE_SINGLE
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_MULTIPLE_BINARY_PROPERTIES_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_MULTIVALUED_PROPERTIES_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_ORDERABLE_CHILD_NODES_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_OVERRIDES_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_PRIMARY_ITEM_NAME_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_PROPERTY_TYPES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_STRING
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_BINARY
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_LONG
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_LONG
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_DOUBLE
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_DECIMAL
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_DATE
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_BOOLEAN
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_NAME
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_PATH
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_REFERENCE
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_WEAKREFERENCE
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_URI
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_UNDEFINED
argument_list|)
block|}
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_RESIDUAL_DEFINITIONS_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_SAME_NAME_SIBLINGS_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_VALUE_CONSTRAINTS_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|NODE_TYPE_MANAGEMENT_UPDATE_IN_USE_SUPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_ACCESS_CONTROL_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_JOURNALED_OBSERVATION_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_LIFECYCLE_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_LOCKING_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_OBSERVATION_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_QUERY_SQL_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_RETENTION_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_SHAREABLE_NODES_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_SIMPLE_VERSIONING_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_TRANSACTIONS_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_UNFILED_CONTENT_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_UPDATE_MIXIN_NODE_TYPES_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_UPDATE_PRIMARY_NODE_TYPE_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_VERSIONING_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_XML_EXPORT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_XML_IMPORT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_ACTIVITIES_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|OPTION_BASELINES_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_FULL_TEXT_SEARCH_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_JOINS
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
name|QUERY_JOINS_NONE
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_LANGUAGES
argument_list|,
operator|new
name|Value
index|[
literal|0
index|]
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_STORED_QUERIES_SUPPORTED
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_XPATH_DOC_ORDER
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|QUERY_XPATH_POS_INDEX
argument_list|,
name|falseValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|REP_NAME_DESC
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"Apache Jackrabbit Oak JCR implementation"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|REP_VENDOR_DESC
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"Apache Software Foundation"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|REP_VENDOR_URL_DESC
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"http://www.apache.org/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|SPEC_NAME_DESC
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"Content Repository for Java Technology API"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|SPEC_VERSION_DESC
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"2.0"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
operator|new
name|Descriptor
argument_list|(
name|WRITE_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Descriptors
parameter_list|(
name|ValueFactory
name|valueFactory
parameter_list|,
name|Iterable
argument_list|<
name|Descriptor
argument_list|>
name|descriptors
parameter_list|)
block|{
name|this
argument_list|(
name|valueFactory
argument_list|)
expr_stmt|;
for|for
control|(
name|Descriptor
name|d
range|:
name|descriptors
control|)
block|{
name|this
operator|.
name|descriptors
operator|.
name|put
argument_list|(
name|d
operator|.
name|name
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
index|[]
name|getKeys
parameter_list|()
block|{
return|return
name|descriptors
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|descriptors
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isStandardDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|&&
name|descriptors
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|standard
return|;
block|}
specifier|public
name|boolean
name|isSingleValueDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|&&
name|descriptors
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|singleValued
return|;
block|}
specifier|public
name|Value
name|getValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Descriptor
name|d
init|=
name|descriptors
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|null
operator|||
operator|!
name|d
operator|.
name|singleValued
condition|?
literal|null
else|:
name|d
operator|.
name|values
index|[
literal|0
index|]
return|;
block|}
specifier|public
name|Value
index|[]
name|getValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Descriptor
name|d
init|=
name|descriptors
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|null
condition|?
literal|null
else|:
name|d
operator|.
name|values
return|;
block|}
specifier|public
specifier|static
specifier|final
class|class
name|Descriptor
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|Value
index|[]
name|values
decl_stmt|;
specifier|final
name|boolean
name|singleValued
decl_stmt|;
specifier|final
name|boolean
name|standard
decl_stmt|;
specifier|public
name|Descriptor
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
index|[]
name|values
parameter_list|,
name|boolean
name|singleValued
parameter_list|,
name|boolean
name|standard
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
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|singleValued
operator|=
name|singleValued
expr_stmt|;
name|this
operator|.
name|standard
operator|=
name|standard
expr_stmt|;
block|}
specifier|public
name|Descriptor
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
name|value
parameter_list|,
name|boolean
name|singleValued
parameter_list|,
name|boolean
name|standard
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|Value
index|[]
block|{
name|value
block|}
argument_list|,
name|singleValued
argument_list|,
name|standard
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------< private>---
specifier|private
name|void
name|put
parameter_list|(
name|Descriptor
name|descriptor
parameter_list|)
block|{
name|descriptors
operator|.
name|put
argument_list|(
name|descriptor
operator|.
name|name
argument_list|,
name|descriptor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

