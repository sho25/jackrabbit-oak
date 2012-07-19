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
name|lucene
package|;
end_package

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
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
operator|.
name|*
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
name|StringField
import|;
end_import

begin_comment
comment|/**  *<code>FieldFactory</code> is a factory for<code>Field</code> instances with  * frequently used fields.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FieldFactory
block|{
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
parameter_list|)
block|{
comment|// TODO do we need norms info on the indexed fields ? TextField:StringField
comment|// return new TextField(name, value, NO);
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
block|}
end_class

end_unit

