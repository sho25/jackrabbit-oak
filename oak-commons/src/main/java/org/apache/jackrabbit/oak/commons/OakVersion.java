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
name|commons
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Provides version information about Oak.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|OakVersion
block|{
specifier|private
name|OakVersion
parameter_list|()
block|{     }
comment|/**      * Returns the version of an Oak module.      *       * @param moduleName the name of the module      * @param clazz a class of the module      * @return the version (or "SNAPSHOT" when unknown)      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|(
name|String
name|moduleName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
name|String
name|version
init|=
literal|"SNAPSHOT"
decl_stmt|;
comment|// fallback
name|InputStream
name|stream
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
literal|"/META-INF/maven/org.apache.jackrabbit/"
operator|+
name|moduleName
operator|+
literal|"/pom.properties"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
try|try
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|version
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"version"
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|version
return|;
block|}
block|}
end_class

end_unit

