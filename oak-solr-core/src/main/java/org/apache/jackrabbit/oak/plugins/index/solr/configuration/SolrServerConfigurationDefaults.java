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
name|solr
operator|.
name|configuration
package|;
end_package

begin_comment
comment|/**  * Defaults for Solr server configurations.  */
end_comment

begin_class
specifier|public
class|class
name|SolrServerConfigurationDefaults
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_HOME_PATH
init|=
literal|"solr"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_CONFIG_PATH
init|=
literal|"solr.xml"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NAME
init|=
literal|"oak"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_PORT
init|=
literal|"8983"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_BASE_URL
init|=
literal|"http://127.0.0.1"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONTEXT
init|=
literal|"/solr"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATH_FIELD_NAME
init|=
literal|"path_exact"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CHILD_FIELD_NAME
init|=
literal|"path_child"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESC_FIELD_NAME
init|=
literal|"path_des"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ANC_FIELD_NAME
init|=
literal|"path_anc"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CATCHALL_FIELD
init|=
literal|"catch_all"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ROWS
init|=
literal|100000
decl_stmt|;
block|}
end_class

end_unit

