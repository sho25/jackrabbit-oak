begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|TextField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
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
name|FieldNames
operator|.
name|PATH
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
name|FieldNames
operator|.
name|FULLTEXT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
operator|.
name|NO
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
operator|.
name|YES
import|;
end_import

begin_comment
comment|/**  * {@code FieldFactory} is a factory for<code>Field</code> instances with  * frequently used fields.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FieldFactory
block|{
comment|/**      * StringField#TYPE_NOT_STORED but tokenized      */
specifier|private
specifier|static
specifier|final
name|FieldType
name|OAK_TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FieldType
name|OAK_TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|OAK_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|OAK_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OAK_TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|OakTextField
extends|extends
name|Field
block|{
specifier|public
name|OakTextField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|stored
condition|?
name|OAK_TYPE
else|:
name|OAK_TYPE_NOT_STORED
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Private constructor.      */
specifier|private
name|FieldFactory
parameter_list|()
block|{     }
specifier|public
specifier|static
name|Field
name|newPathField
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|StringField
argument_list|(
name|PATH
argument_list|,
name|path
argument_list|,
name|YES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Field
name|newPropertyField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|tokenized
parameter_list|,
name|boolean
name|stored
parameter_list|)
block|{
if|if
condition|(
name|tokenized
condition|)
block|{
return|return
operator|new
name|OakTextField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|stored
argument_list|)
return|;
block|}
return|return
operator|new
name|StringField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|NO
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Field
name|newFulltextField
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|TextField
argument_list|(
name|FULLTEXT
argument_list|,
name|value
argument_list|,
name|NO
argument_list|)
return|;
block|}
block|}
end_class

end_unit

