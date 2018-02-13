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
name|segment
operator|.
name|file
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
name|segment
operator|.
name|SegmentNodeStorePersistence
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
name|segment
operator|.
name|SegmentNodeStorePersistence
operator|.
name|ManifestFile
import|;
end_import

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
name|util
operator|.
name|Properties
import|;
end_import

begin_class
class|class
name|Manifest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|STORE_VERSION
init|=
literal|"store.version"
decl_stmt|;
comment|/**      * Loads the manifest from a file.      *      * @param file The file to load the manifest from.      * @return A manifest file.      * @throws IOException If any error occurs when loading the manifest.      */
specifier|static
name|Manifest
name|load
parameter_list|(
name|ManifestFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Manifest
argument_list|(
name|file
operator|.
name|load
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates an empty manifest file.      *      * @return A manifest file.      */
specifier|static
name|Manifest
name|empty
parameter_list|()
block|{
return|return
operator|new
name|Manifest
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|Properties
name|properties
decl_stmt|;
specifier|private
name|Manifest
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
comment|/**      * Return the store version saved in this manifest or a user provided value      * if no valid store version is saved in the manifest.      *      * @param otherwise The value that will be returned if no valid store      *                  version is saved in this manifest.      * @return The store version stored in this manifest or the user provided      * default value.      */
name|int
name|getStoreVersion
parameter_list|(
name|int
name|otherwise
parameter_list|)
block|{
return|return
name|getIntegerProperty
argument_list|(
name|STORE_VERSION
argument_list|,
name|otherwise
argument_list|)
return|;
block|}
comment|/**      * Set the store version in this manifest.      *      * @param version The store version.      */
name|void
name|setStoreVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|setIntegerProperty
argument_list|(
name|STORE_VERSION
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**      * Save the manifest to the specified file.      *      * @param file The file to save the manifest to.      * @throws IOException if an error occurs while saving the manifest.      */
name|void
name|save
parameter_list|(
name|ManifestFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|save
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getIntegerProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|otherwise
parameter_list|)
block|{
name|Object
name|value
init|=
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|otherwise
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
name|otherwise
return|;
block|}
block|}
specifier|private
name|void
name|setIntegerProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

