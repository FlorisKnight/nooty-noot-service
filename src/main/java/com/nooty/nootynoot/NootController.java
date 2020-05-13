package com.nooty.nootynoot;

import com.google.gson.Gson;
import com.nooty.nootynoot.messaging.HashtagSender;
import com.nooty.nootynoot.messaging.LiveNootsSender;
import com.nooty.nootynoot.models.Noot;
import com.nooty.nootynoot.viewmodels.CreateViewModel;
import com.nooty.nootynoot.viewmodels.GetFromUserViewModel;
import com.nooty.nootynoot.viewmodels.GetTimelineViewModel;
import com.nooty.nootynoot.viewmodels.HashtagViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/noot")
public class NootController {
    @Autowired
    private NootRepo nootRepo;
    private HashtagSender hashtagSender;
    private LiveNootsSender liveNootsSender;
    private Gson gson;

    public NootController() {
        this.hashtagSender = new HashtagSender();
        this.liveNootsSender = new LiveNootsSender();
        this.gson = new Gson();
    }

    @PostMapping(path = "/create", produces = "application/json")
    public ResponseEntity create(@RequestBody CreateViewModel createViewModel) {
        Noot noot = new Noot();
        noot.setId(UUID.randomUUID().toString());
        noot.setText(createViewModel.getText());
        noot.setTimestamp(createViewModel.getTimestamp());
        noot.setUserId(createViewModel.getUserId());

        //TODO check text for hashtag and send to hashtag rabbitmq que
        //TODO send to subscriber on rabbitmq que

        this.nootRepo.save(noot);

        liveNootsSender.sendNootToSubs(gson.toJson(noot));

        List<String> hashTags = checkHashtag(noot);
        if (hashTags.size() != 0) {
            for (String h: hashTags) {
                HashtagViewModel hvm = new HashtagViewModel();
                hvm.getUserId();
                hvm.setHashtag(h);
                hashtagSender.newHashtag(gson.toJson(hvm));
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity get(@PathVariable String id) {
        Optional<Noot> nootOptional = this.nootRepo.findById(id);
        if (!nootOptional.isPresent()) {
            return ResponseEntity.status(404).build();
        }

        Noot noot = nootOptional.get();
        return ResponseEntity.ok(noot);
    }

    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity getAll() {
        List<Noot> noots = new ArrayList<Noot>();
        this.nootRepo.findAll().forEach(noot -> {
            noots.add(noot);
        });

        return ResponseEntity.ok(noots);
    }

    @GetMapping(path = "user/{id}", produces = "application/json")
    public ResponseEntity getFromUser(@PathVariable String id, @RequestBody GetFromUserViewModel getFromUserViewModel) {
        Optional<Noot> nootOptional = this.nootRepo.findById(id);
        if (!nootOptional.isPresent()) {
            return ResponseEntity.status(404).build();
        }

        Noot noot = nootOptional.get();
        return ResponseEntity.ok(noot);
    }

    @GetMapping(path = "timeline/{id}", produces = "application/json")
    public ResponseEntity getTimeLine(@PathVariable String id, @RequestBody GetTimelineViewModel getTimelineViewModel) {
        List<Noot> noots = new ArrayList<Noot>();

        this.nootRepo.findAll().forEach(noot -> {
            noots.add(noot);
        });

        int startIndex = getStartIndex(noots, getTimelineViewModel.getLastId());
        List<Noot> nootsTimeline = getSelectNootsTimeline(noots, startIndex, getTimelineViewModel.getUserIds(), getTimelineViewModel.getAmmount());

        return ResponseEntity.ok(nootsTimeline);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity delete(@PathVariable String id) {
        Optional<Noot> nootOptional = this.nootRepo.findById(id);
        if (!nootOptional.isPresent()) {
            return ResponseEntity.status(404).build();
        }
        Noot noot = nootOptional.get();
        this.nootRepo.delete(noot);

        List<String> hashTags = checkHashtag(noot);
        if (hashTags.size() != 0) {
            for (String h: hashTags) {
                HashtagViewModel hvm = new HashtagViewModel();
                hvm.getUserId();
                hvm.setHashtag(h);
                hashtagSender.deleteHashtag(gson.toJson(hvm));
            }
        }

        return ResponseEntity.ok().build();
    }

    private int getStartIndex(List<Noot> noots, String id) {
        int indexList;
        for (indexList = 0; noots.size() > indexList ;indexList++) {
            Noot n = noots.get(indexList);
            if (noots.get(indexList).getId().equals(id)) {
                return indexList;
            }
        }
        return 0;
    }

    private List<Noot> getSelectNootsTimeline(List<Noot> noots, int startIndex, ArrayList<String> userIds, int ammount) {
        List<Noot> nootsTimeline = new ArrayList<Noot>();
        for (int i = startIndex; i < noots.size() || i < ammount; i++) {
            if (checkIfNootContainsUserId(noots.get(i).getUserId(), userIds)) {
                nootsTimeline.add(noots.get(i));
            }
        }
        return nootsTimeline;
    }

    private boolean checkIfNootContainsUserId(String userId, ArrayList<String> userIds) {
        for (String id: userIds) {
            if (id.equals(userId)){
                return true;
            }
        }
        return false;
    }

    private List<String> checkHashtag(Noot noot) {
        Pattern patt = Pattern.compile("(#\\w+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher match = patt.matcher(noot.getText());
        List<String> matStr = new ArrayList<String>();
        while (match.find()) {
            for (String m: matStr) {
                if (!m.equals(match.group(1))) {
                    matStr.add(match.group(1));
                }
            }
        }
        return matStr;
    }
}
