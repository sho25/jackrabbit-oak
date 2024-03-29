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
operator|.
name|property
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|checkState
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
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROPERTY_INDEX
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
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_STORAGE_TYPE
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
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|STORAGE_TYPE_UNIQUE
import|;
end_import

begin_comment
comment|/**  * Performs simple property=value query against a unique property index storage  */
end_comment

begin_class
class|class
name|PropertyIndexQuery
implements|implements
name|PropertyQuery
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|public
name|PropertyIndexQuery
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getIndexedPaths
parameter_list|(
name|String
name|propertyRelativePath
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|NodeBuilder
name|idxb
init|=
name|getIndexNode
argument_list|(
name|propertyRelativePath
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|STORAGE_TYPE_UNIQUE
operator|.
name|equals
argument_list|(
name|idxb
operator|.
name|getString
argument_list|(
name|PROP_STORAGE_TYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|entry
init|=
name|idxb
operator|.
name|child
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|entry
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
return|;
block|}
specifier|private
name|NodeBuilder
name|getIndexNode
parameter_list|(
name|String
name|propertyRelativePath
parameter_list|)
block|{
name|NodeBuilder
name|propertyIndex
init|=
name|builder
operator|.
name|child
argument_list|(
name|PROPERTY_INDEX
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|HybridPropertyIndexUtil
operator|.
name|getNodeName
argument_list|(
name|propertyRelativePath
argument_list|)
decl_stmt|;
return|return
name|propertyIndex
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

