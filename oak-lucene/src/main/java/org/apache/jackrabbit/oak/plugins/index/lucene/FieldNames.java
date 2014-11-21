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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Defines field names that are used internally to store :path, etc in the  * search index.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FieldNames
block|{
comment|/**      * Private constructor.      */
specifier|private
name|FieldNames
parameter_list|()
block|{     }
comment|/**      * Name of the field that contains the {@value} property of the node.      */
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|":path"
decl_stmt|;
comment|/**      * Name of the field that contains the fulltext index.      */
specifier|public
specifier|static
specifier|final
name|String
name|FULLTEXT
init|=
literal|":fulltext"
decl_stmt|;
comment|/**      * Prefix for all field names that are fulltext indexed by property name.      */
specifier|public
specifier|static
specifier|final
name|String
name|FULLTEXT_PREFIX
init|=
literal|":full"
decl_stmt|;
comment|/**      * Used to select only the PATH field from the lucene documents      */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PATH_SELECTOR
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|PATH
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Encodes the field name such that it can be used for storing DocValue      * This is done such a field if used for both sorting and querying uses      * a different name for docvalue field      *      * @param name name to encode      * @return encoded field name      */
specifier|public
specifier|static
name|String
name|createDocValFieldName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|":dv"
operator|+
name|name
return|;
block|}
specifier|public
specifier|static
name|String
name|createAnalyzedFieldName
parameter_list|(
name|String
name|pname
parameter_list|)
block|{
return|return
name|FULLTEXT_PREFIX
operator|+
name|pname
return|;
block|}
block|}
end_class

end_unit

