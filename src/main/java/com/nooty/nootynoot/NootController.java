package com.nooty.nootynoot;

import com.nooty.nootynoot.models.Noot;
import com.nooty.nootynoot.viewmodels.CreateViewModel;
import com.nooty.nootynoot.viewmodels.GetFromUserViewModel;
import com.nooty.nootynoot.viewmodels.GetTimelineViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/noot")
public class NootController {
    @Autowired
    private NootRepo nootRepo;

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

        //TODO send message to rabbitmq hashtag delete que

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
}
