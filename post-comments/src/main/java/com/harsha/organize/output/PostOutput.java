package com.harsha.organize.output;

import java.util.List;

public record PostOutput(int id,int user_id, String title, List<CommentOutput>commentDataList) {
}
