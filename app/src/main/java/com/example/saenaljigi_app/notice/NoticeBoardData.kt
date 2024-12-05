data class NoticeBoardData(
    val noticeId: Long,
    val title: String,
    val date: String,
    var content: String? = null,
    var author: String? = null,
    var files: List<FileData>? = null,  // 파일 목록 필드
    var file1: String? = null,  // file1 필드 추가
    var file2: String? = null   // file2 필드 추가
) {
    // file1, file2를 List<FileData>로 변환하는 메서드
    fun populateFiles() {
        val fileList = mutableListOf<FileData>()

        // file1과 file2를 파일 목록으로 변환
        file1?.let { fileList.add(FileData("file1", it)) }
        file2?.let { fileList.add(FileData("file2", it)) }

        // 변환된 파일 목록을 files에 할당
        files = fileList
    }
}

data class FileData(
    val fileName: String,  // 파일 이름 필드
    val fileUrl: String
)
