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
name|upgrade
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|Util
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Util
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Util
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|unzip
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|File
name|targetDir
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Unzipping to {}"
argument_list|,
name|targetDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ZipInputStream
name|zis
init|=
operator|new
name|ZipInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
try|try
block|{
name|ZipEntry
name|entry
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zis
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|target
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|OutputStream
name|output
init|=
operator|new
name|FileOutputStream
argument_list|(
name|target
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|zis
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|zis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|delta
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|delta
operator|>
literal|1000L
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unzip took {} msec"
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

