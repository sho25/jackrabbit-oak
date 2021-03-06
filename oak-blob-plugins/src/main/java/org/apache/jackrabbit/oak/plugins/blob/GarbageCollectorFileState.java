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
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|IOException
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
name|FileUtils
import|;
end_import

begin_comment
comment|/**  * Class for keeping the file system state of the garbage collection.  *   * Also, manages any temporary files needed as well as external sorting.  *   */
end_comment

begin_class
specifier|public
class|class
name|GarbageCollectorFileState
implements|implements
name|Closeable
block|{
comment|/** The root of the gc file state directory. */
specifier|private
specifier|final
name|File
name|home
decl_stmt|;
comment|/** The marked references. */
specifier|private
specifier|final
name|File
name|markedRefs
decl_stmt|;
comment|/** The available references. */
specifier|private
specifier|final
name|File
name|availableRefs
decl_stmt|;
comment|/** The gc candidates. */
specifier|private
specifier|final
name|File
name|gcCandidates
decl_stmt|;
comment|/** The garbage stores the garbage collection candidates which were not deleted . */
specifier|private
specifier|final
name|File
name|garbage
decl_stmt|;
comment|/**      * Instantiates a new garbage collector file state.      *       * @param root path of the root directory under which the      *             files created during gc are stored      */
specifier|public
name|GarbageCollectorFileState
parameter_list|(
name|String
name|root
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|home
operator|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
literal|"gcworkdir-"
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|markedRefs
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"marked-"
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|availableRefs
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"avail-"
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|gcCandidates
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"gccand-"
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|garbage
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"gc-"
operator|+
name|startTime
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|home
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the file storing the marked references.      *       * @return the marked references      */
specifier|public
name|File
name|getMarkedRefs
parameter_list|()
block|{
return|return
name|markedRefs
return|;
block|}
comment|/**      * Gets the file storing the available references.      *       * @return the available references      */
specifier|public
name|File
name|getAvailableRefs
parameter_list|()
block|{
return|return
name|availableRefs
return|;
block|}
comment|/**      * Gets the file storing the gc candidates.      *       * @return the gc candidates      */
specifier|public
name|File
name|getGcCandidates
parameter_list|()
block|{
return|return
name|gcCandidates
return|;
block|}
comment|/**      * Gets the storing the garbage.      *       * @return the garbage      */
specifier|public
name|File
name|getGarbage
parameter_list|()
block|{
return|return
name|garbage
return|;
block|}
comment|/**      * Completes the process by deleting the files.      *       * @throws IOException      *             Signals that an I/O exception has occurred.      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|getGarbage
argument_list|()
operator|.
name|exists
argument_list|()
operator|||
name|FileUtils
operator|.
name|sizeOf
argument_list|(
name|getGarbage
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|home
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

