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
package|;
end_package

begin_interface
specifier|public
interface|interface
name|IndexConstants
block|{
name|String
name|INDEX_DEFINITIONS_NODE_TYPE
init|=
literal|"oak:queryIndexDefinition"
decl_stmt|;
name|String
name|INDEX_DEFINITIONS_NAME
init|=
literal|"oak:index"
decl_stmt|;
name|String
name|TYPE_PROPERTY_NAME
init|=
literal|"type"
decl_stmt|;
name|String
name|TYPE_UNKNOWN
init|=
literal|"unknown"
decl_stmt|;
name|String
name|REINDEX_PROPERTY_NAME
init|=
literal|"reindex"
decl_stmt|;
name|String
name|INDEX_DATA_CHILD_NAME
init|=
literal|":data"
decl_stmt|;
comment|//TODO remove this property as soon as the index manager is in
name|String
name|DEFAULT_INDEX_HOME
init|=
literal|"/"
decl_stmt|;
block|}
end_interface

end_unit

