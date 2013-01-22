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
name|index
operator|.
name|old
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|IndexUtils
import|;
end_import

begin_interface
specifier|public
interface|interface
name|PropertyIndexConstants
block|{
name|String
name|INDEX_TYPE_PROPERTY
init|=
literal|"property"
decl_stmt|;
comment|/**      * The root node of the index definition (configuration) nodes.      */
comment|// TODO OAK-178 discuss where to store index config data
name|String
name|INDEX_CONFIG_PATH
init|=
literal|"/"
operator|+
name|IndexUtils
operator|.
name|INDEX_DEFINITIONS_NAME
operator|+
literal|"/indexes"
decl_stmt|;
comment|// "/jcr:system/indexes";
comment|/**      * For each index, the index content is stored relative to the index      * definition below this node. There is also such a node just below the      * index definition node, to store the last revision and for temporary data.      */
name|String
name|INDEX_CONTENT
init|=
literal|":data"
decl_stmt|;
comment|/**      * The node name prefix of a prefix index.      */
name|String
name|TYPE_PREFIX
init|=
literal|"prefix@"
decl_stmt|;
comment|/**      * The node name prefix of a property index.      */
comment|// TODO support multi-property indexes
name|String
name|TYPE_PROPERTY
init|=
literal|"property@"
decl_stmt|;
comment|/**      * Marks a unique index.      */
name|String
name|UNIQUE
init|=
name|IndexConstants
operator|.
name|UNIQUE
decl_stmt|;
block|}
end_interface

end_unit

