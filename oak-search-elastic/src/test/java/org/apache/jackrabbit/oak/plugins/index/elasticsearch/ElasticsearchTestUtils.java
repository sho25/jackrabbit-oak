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
name|elasticsearch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_class
class|class
name|ElasticsearchTestUtils
block|{
specifier|private
specifier|static
name|String
name|createHealthURL
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|ElasticsearchCoordinate
name|esCoord
parameter_list|)
block|{
return|return
name|esCoord
operator|.
name|getScheme
argument_list|()
operator|+
literal|"://"
operator|+
name|esCoord
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|esCoord
operator|.
name|getPort
argument_list|()
operator|+
literal|"/_cat/health"
return|;
block|}
specifier|static
name|boolean
name|isAvailable
parameter_list|(
specifier|final
name|ElasticsearchCoordinate
name|esCoord
parameter_list|)
block|{
if|if
condition|(
name|esCoord
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|createHealthURL
argument_list|(
name|esCoord
argument_list|)
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|con
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|int
name|responseCode
init|=
name|con
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
return|return
name|responseCode
operator|==
literal|200
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

